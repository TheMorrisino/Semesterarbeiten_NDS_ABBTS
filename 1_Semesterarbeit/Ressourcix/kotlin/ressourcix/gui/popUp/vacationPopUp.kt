package ressourcix.gui.popUp

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0
private const val BOX_HEIGHT = 300.0
private const val BOX_WIDTH = 600.0

object vacationPopUp {

    var idField = createTfl("","")
    var abbreviationField = createTfl("","")

    var firstVacationWeek = createTfl("","Erste Ferien Woche eintragen...")
    var lastVacationWeek = createTfl("","Letzte Ferien Woche eintragen...")

    fun build(onClose: () -> Unit, onSave: (String) -> Unit = {}): Node =
        VBox().apply {
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
                Label("Ferien eintragen"),
                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(10.0)
                    spacing = 10.0
                    children.addAll(createDataBox("Mitarbeiter ID:",idField),
                    createDataBox("Mitarbeiter Kürzel:",abbreviationField)
                    )
                },
                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(10.0)
                    spacing = 10.0
                    children.addAll(createDataBox("Ferien von Woche:",firstVacationWeek),
                    createDataBox("Ferien bis Woche:",lastVacationWeek)
                    )
                },
                HBox().apply {
                    alignment = Pos.CENTER
                    padding = Insets(20.0)
                    spacing = 80.0
                    children.addAll(
                        createButton("Antrag\nspeichern").apply { setOnAction { //TODO Funktionen einfügen
                            onSave("")
                            onClose()
                        } },
                        createButton("Antrag\nLöschen").apply {setOnAction { onClose() } } //TODO Funktionen einfügen
                    )
                }
            )
        }
}

private fun createButton(text: String) = Button(text).apply {
    prefHeight = BTN_HEIGHT
    prefWidth = BTN_WIDTH
    textAlignment = TextAlignment.CENTER
    alignment = Pos.CENTER
    isFocusTraversable = false
}

private fun createTfl(text: String,prompt: String): TextField =
    TextField(text).apply {
        promptText = prompt
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
        isFocusTraversable = false
    }

private fun createDataBox(labelText: String, field: TextField): VBox =
    VBox(6.0).apply {
        children.addAll(Label(labelText), field)
    }