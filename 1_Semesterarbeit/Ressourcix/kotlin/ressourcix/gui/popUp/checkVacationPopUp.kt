package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment
import ressourcix.app.app
import ressourcix.domain.VacationStatus
import ressourcix.gui.pages.calenderView
import ressourcix.gui.pages.calenderView.selectedEmployee
import ressourcix.gui.pages.calenderView.refreshVacations
import ressourcix.gui.pages.calenderView.showYear

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 160.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0
private const val BOX_HEIGHT = 520.0
private const val BOX_WIDTH = 720.0

object CheckVacationPopUp {

    /* ================= Tabellenmodell ================= */

    data class VacationTableRow(
        val id: UInt,
        val startWeek: UInt,
        val endWeek: UInt,
        val status: VacationStatus
    )

    /* ================= UI Builder ================= */

    fun build(
        onClose: () -> Unit
    ): Node {

        /* ================= Felder ================= */

        val vacationId = createTfl("", "ID").apply {
            isEditable = false
            isDisable = true
        }

        val statusField = ComboBox<VacationStatus>().apply {
            items = FXCollections.observableArrayList(*VacationStatus.values())
            promptText = "Neuen Status auswählen..."
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
            promptWhenNull()
        }

        /* ================= Tabelle ================= */

        val vacationTable = TableView<VacationTableRow>().apply {
            prefHeight = 200.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }

        val colId = TableColumn<VacationTableRow, UInt>("ID").apply {
            setCellValueFactory { SimpleObjectProperty(it.value.id) }
        }

        val colStartWeek = TableColumn<VacationTableRow, UInt>("Startwoche").apply {
            setCellValueFactory { SimpleObjectProperty(it.value.startWeek) }
        }

        val colEndWeek = TableColumn<VacationTableRow, UInt>("Endwoche").apply {
            setCellValueFactory { SimpleObjectProperty(it.value.endWeek) }
        }

        val colStatus = TableColumn<VacationTableRow, VacationStatus>("Status").apply {
            setCellValueFactory { SimpleObjectProperty(it.value.status) }
        }

        vacationTable.columns.addAll(colId, colStartWeek, colEndWeek, colStatus)

        /* ================= Tabelle füllen ================= */

        fun loadTableData() {
            vacationTable.items.setAll(
                selectedEmployee.vacationEntries.map { entry ->
                    VacationTableRow(
                        entry.id,
                        entry.range.startWeek,
                        entry.range.endWeek,
                        entry.status
                    )
                }
            )
        }

        loadTableData()

        /* ================= Tabellenklick ================= */

        vacationTable.selectionModel.selectedItemProperty().addListener { _, _, row ->
            row?.let {
                vacationId.text = it.id.toString()
                statusField.value = it.status
            }
        }

        /* ================= Fehleranzeige ================= */

        val errorLabel = Label().apply {
            style = "-fx-text-fill: red;"
        }

        fun validate(): String? {
            if (vacationId.text.isBlank())
                return "Bitte einen Eintrag aus der Tabelle auswählen"

            if (statusField.value == null)
                return "Bitte einen neuen Status auswählen"

            return null
        }

        fun updateError() {
            errorLabel.text = validate().orEmpty()
        }

        statusField.valueProperty().addListener { _, _, _ -> updateError() }

        /* ================= Save Button ================= */

        val saveBtn = createButton("Status\nändern").apply {

            disableProperty().bind(
                Bindings.createBooleanBinding(
                    { validate() != null },
                    vacationId.textProperty(),
                    statusField.valueProperty()
                )
            )

            setOnAction {
                val error = validate()
                if (error != null) {
                    errorLabel.text = error
                    return@setOnAction
                }

                val vacationIdValue = vacationId.text.toUInt()
                val newStatus = statusField.value

                val realEmployee = app.management.employees
                    .find { it.getId() == selectedEmployee.getId() }
                    ?: return@setOnAction

                realEmployee.vacationEntries
                    .find { it.id == vacationIdValue }
                    ?.apply {
                        status = newStatus
                    }

                loadTableData()
            }


        }

        /* ================= Close Button ================= */

        val closeBtn = createButton("X").apply {
            style = """
                -fx-background-color: transparent;
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                -fx-padding: 2 8 2 8;
            """.trimIndent()
            prefWidth = 40.0
            isFocusTraversable = false
            alignment = Pos.TOP_RIGHT
            setOnAction {
                refreshVacations()
                onClose() }
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
            padding = Insets(12.0)
            spacing = 10.0
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
                vacationTable,

                HBox(20.0).apply {
                    alignment = Pos.CENTER
                    padding = Insets(10.0)
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

                errorLabel,

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
