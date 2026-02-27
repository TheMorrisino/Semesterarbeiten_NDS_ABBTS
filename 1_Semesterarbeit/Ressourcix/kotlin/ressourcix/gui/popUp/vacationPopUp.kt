// Autor Tiago

package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import ressourcix.app.app
import ressourcix.gui.pages.calenderView.closePopup
import ressourcix.gui.pages.calenderView.selectedEmployee
import ressourcix.gui.pages.calenderView.showPopup
import ressourcix.gui.util.*

private const val BOX_HEIGHT = 340.0
private const val BOX_WIDTH = 600.0

object vacationPopUp {

    data class vacationRequestWK(val startKW: UInt, val endKW: UInt)

    var idField = createTfl("", "")
    var nameField = createTfl("", "")


    fun build(onClose: () -> Unit, onRemove: (vacationRequestWK) -> Unit, onSave: (vacationRequestWK) -> Unit): Node {
        var firstVacationWeek = createTfl("", "Erste Ferien Woche eintragen...")
        var lastVacationWeek = createTfl("", "Letzte Ferien Woche eintragen...")

        numbersOnly(firstVacationWeek)
        numbersOnly(lastVacationWeek)

        val errorLabel = Label().apply {
            style = "-fx-text-fill: red;"

        }

        var validDeleteBtn = false
        fun validate(): String? {
            val start = firstVacationWeek.text.toIntOrNull()
            val end = lastVacationWeek.text.toIntOrNull()
            validDeleteBtn = false
            errorLabel.style = "-fx-text-fill: red;"
            if (start == null || end == null) return "Bitte Start- und Endwoche ausfüllen"
            if (start !in 1..52) return "Start-KW muss 1–52 sein"
            if (end !in 1..52) return "End-KW muss 1–52 sein"
            if (end < start) return "End-KW darf nicht kleiner sein als Start-KW"
            val startWeek = start ?: return null
            val endWeek = end ?: return null
            val employee = app.management.employees
                .find { it.getId() == selectedEmployee.getId() }
            if (employee == null) {
                return "Mitarbeiter wurde nicht gefunden"
            }
            if (selectedEmployee.findEntryByStartweek(start.toUInt())) {
                validDeleteBtn = true
                errorLabel.style = "-fx-text-fill: orange;"
                return "Ferieneintrag kann gelöscht werden"
                }
            if (!app.management.canAddVacation(
                    employee,
                    startWeek.toUInt(),
                    endWeek.toUInt()
                )
            ) {
                return "Es wurde eine Überschneidung mit den bereits beantragten Ferien erkannt"
            }

            return null
        }

        fun updateError() {
            errorLabel.text = validate().orEmpty()
        }

        val saveBtn = createButton("Antrag\nspeichern").apply {
            disableProperty().bind(
                Bindings.createBooleanBinding(
                    { validate() != null },
                    firstVacationWeek.textProperty(),
                    lastVacationWeek.textProperty()
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
                        firstVacationWeek.text.toUInt(),
                        lastVacationWeek.text.toUInt()
                    )
                )
                onClose()
            }
        }

        val deleteBtn = createButton("Antrag\nLöschen").apply {
            disableProperty().bind(
                Bindings.createBooleanBinding(
                    { !validDeleteBtn },
                    firstVacationWeek.textProperty(),
                    lastVacationWeek.textProperty()
                )
            )
            setOnAction {
            if (!validDeleteBtn) {
                updateError()
                return@setOnAction
            }
                onRemove(
                    vacationRequestWK(
                        firstVacationWeek.text.toUInt(),
                        lastVacationWeek.text.toUInt()
                    )
                )
                onClose()
            }

        }

        val checkBtn = createButton("Antrag\nBearbeiten").apply {
            setOnAction {
                showPopup(
                    checkVacationPopUp.build(
                        onClose = { closePopup() }
                    )
                )
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
            setOnAction { onClose()}
        }
        val title = Label("Ferien eintragen").apply {
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

        firstVacationWeek.textProperty().addListener { _, _, _ -> updateError() }
        lastVacationWeek.textProperty().addListener { _, _, _ -> updateError() }
        updateError()

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
                    spacing = 10.0
                    children.addAll(
                        createDataBox("Mitarbeiter ID:", idField).apply {
                            isDisable = true
                        },
                        createDataBox("Namen des Mitarbeiters:", nameField).apply {
                            isDisable = true
                        }
                    )
                },
                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(10.0)
                    spacing = 10.0
                    children.addAll(
                        createDataBox("Ferien von Woche:", firstVacationWeek),
                        createDataBox("Ferien bis Woche:", lastVacationWeek)
                    )
                },
                errorLabel.apply {
                    padding = Insets(0.0,10.0,0.0,10.0)
                },
                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(20.0)
                    spacing = 80.0
                    children.addAll(saveBtn, deleteBtn, checkBtn)
                }
            )
        }
    }

    private fun numbersOnly(tf: TextField) {
        tf.textFormatter = TextFormatter<String> { change ->
            if (change.controlNewText.matches(Regex("\\d*"))) change else null
        }
    }
}


