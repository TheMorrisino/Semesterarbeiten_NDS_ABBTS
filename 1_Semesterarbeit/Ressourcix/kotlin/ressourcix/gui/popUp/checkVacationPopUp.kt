package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.domain.VacationStatus

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0
private const val BOX_HEIGHT = 340.0
private const val BOX_WIDTH = 600.0

object checkVacationPopUp {

    data class vacationRequestWK(
        val idToCheck: UInt,
        val status: VacationStatus
    )

    fun build(
        onClose: () -> Unit,
        onSave: (vacationRequestWK) -> Unit
    ): Node {

        /* ================= Eingabefelder ================= */

        val vacationId = createTfl("", "Ferien ID eintragen...")
        numbersOnly(vacationId)

        val statusField = ComboBox<VacationStatus>().apply {
            items = FXCollections.observableArrayList(*VacationStatus.values())
            promptText = "Status auswählen..."
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
            promptWhenNull()
        }

        /* ================= Fehleranzeige ================= */

        val errorLabel = Label().apply {
            style = "-fx-text-fill: red;"
        }

        fun validate(): String? {
            val id = vacationId.text.toUIntOrNull()
                ?: return "Bitte gültige Ferien-ID eingeben"

            if (id !in 1u..1000u) return "Ferien-ID muss zwischen 1 und 1000 liegen"
            if (statusField.value == null) return "Bitte Status auswählen"

            return null
        }

        fun updateError() {
            errorLabel.text = validate().orEmpty()
        }

        vacationId.textProperty().addListener { _, _, _ -> updateError() }
        statusField.valueProperty().addListener { _, _, _ -> updateError() }

        /* ================= Buttons ================= */

        val saveBtn = createButton("Antrag\nspeichern").apply {
            disableProperty().bind(
                Bindings.createBooleanBinding(
                    { validate() != null },
                    vacationId.textProperty(),
                    statusField.valueProperty()
                )
            )

            setOnAction {
                val err = validate()
                if (err != null) {
                    updateError()
                    return@setOnAction
                }

                onSave(
                    vacationRequestWK(
                        vacationId.text.toUInt(),
                        statusField.value
                    )
                )
                onClose()
            }
        }

        val closeBtn = createButton("X").apply {
            style = """
                -fx-background-color: transparent;
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                -fx-padding: 2 8 2 8;
            """.trimIndent()
            isFocusTraversable = false
            alignment = Pos.TOP_RIGHT
            setOnAction { onClose() }
        }

        /* ================= Header ================= */

        val title = Label("Ferienstatus ändern").apply {
            style = "-fx-font-size: 18px; -fx-font-weight: bold;"
        }

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        val header = HBox().apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(0.0, 0.0, 10.0, 0.0)
            children.addAll(title, spacer, closeBtn)
        }

        updateError()

        /* ================= Layout ================= */

        return VBox().apply {
            padding = Insets(10.0)
            maxWidth = BOX_WIDTH
            maxHeight = BOX_HEIGHT
            style = """
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #cccccc;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 4);
            """.trimIndent()

            children.addAll(
                header,

                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(10.0)
                    spacing = 20.0
                    children.addAll(
                        createDataBox("Ferien ID:", vacationId),
                        VBox(6.0).apply {
                            children.addAll(
                                Label("Status:").apply {
                                    style = "-fx-font-weight: bold;"
                                },
                                statusField
                            )
                        }
                    )
                },

                errorLabel.apply {
                    padding = Insets(0.0, 10.0, 0.0, 10.0)
                },

                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(20.0)
                    children.add(saveBtn)
                }
            )
        }
    }

    /* ================= Helper ================= */

    private fun createButton(text: String) = Button(text).apply {
        prefHeight = BTN_HEIGHT
        prefWidth = BTN_WIDTH
        textAlignment = TextAlignment.CENTER
        alignment = Pos.CENTER
        isFocusTraversable = false
        style = "-fx-font-weight: bold;"
    }

    private fun createTfl(text: String, prompt: String): TextField =
        TextField(text).apply {
            promptText = prompt
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
        }

    private fun createDataBox(labelText: String, field: TextField): VBox =
        VBox(6.0).apply {
            children.addAll(
                Label(labelText).apply {
                    style = "-fx-font-weight: bold;"
                },
                field
            )
        }

    private fun numbersOnly(tf: TextField) {
        tf.textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("\\d*"))) change else null
        }
    }

    private fun <T> ComboBox<T>.promptWhenNull() {
        val p = promptText
        buttonCell = object : ListCell<T>() {
            override fun updateItem(item: T?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty || item == null) p else item.toString()
            }
        }
    }
}
