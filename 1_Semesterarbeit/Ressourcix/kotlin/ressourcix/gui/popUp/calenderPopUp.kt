package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import ressourcix.calendar.consoleCalendarOutput

data class FerienantragKW(val startKW: UInt, val endKW: UInt)
object FerienantragKwPopup {
    var empId: UInt = 0u
    fun show(): FerienantragKW? {
        val dialog = Dialog<FerienantragKW>()

        dialog.title = "Ferienantrag (KW) ${consoleCalendarOutput.getYear()}"
        dialog.headerText = "Start- und Endwoche eingeben von Mitarbeiter ${empId}"

        val okType = ButtonType("OK", ButtonBar.ButtonData.OK_DONE)
        dialog.dialogPane.buttonTypes.addAll(okType, ButtonType.CANCEL)

        val startField = TextField().apply { promptText = "1 - 52" }
        val endField = TextField().apply { promptText = "1 - 52" }

        // nur Zahlen erlauben
        fun numbersOnly(tf: TextField) {
            tf.textFormatter = TextFormatter<String> { change ->
                if (change.controlNewText.matches(Regex("\\d*"))) change else null
            }
        }
        numbersOnly(startField)
        numbersOnly(endField)

        val errorLabel = Label().apply {
            style = "-fx-text-fill: red;"
        }

        val grid = GridPane().apply {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(15.0)

            add(Label("Start-KW:"), 0, 0)
            add(startField, 1, 0)
            add(Label("End-KW:"), 0, 1)
            add(endField, 1, 1)
            add(errorLabel, 0, 2, 2, 1)
        }

        dialog.dialogPane.content = grid

        val okButton = dialog.dialogPane.lookupButton(okType) as Button

        fun validate(): String? {
            val start = startField.text.toIntOrNull()
            val end = endField.text.toIntOrNull()

            if (start == null || end == null) return "Beide Felder ausfüllen"
            if (start !in 1..52) return "Start-KW muss 1–52 sein"
            if (end !in 1..52) return "End-KW muss 1–52 sein"
            if (end < start) return "End-KW darf nicht kleiner sein als Start-KW"
            return null
        }

        okButton.disableProperty().bind(
            Bindings.createBooleanBinding(
                { validate() != null },
                startField.textProperty(),
                endField.textProperty()
            )
        )

        fun updateError() {
            errorLabel.text = validate().orEmpty()
        }
        startField.textProperty().addListener { _, _, _ -> updateError() }
        endField.textProperty().addListener { _, _, _ -> updateError() }

        dialog.setResultConverter { button ->
            if (button == okType) {
                FerienantragKW(
                    startField.text.toUInt(),
                    endField.text.toUInt()
                )
            } else null
        }

        return dialog.showAndWait().orElse(null)
    }
}
