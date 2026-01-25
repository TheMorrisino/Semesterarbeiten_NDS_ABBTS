package ressourcix.gui.popUp

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.text.TextAlignment

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val BOX_HEIGHT = 250.0
private const val BOX_WIDTH = 520.0

object filterPopUp {

    val departmentField = ComboBox<String>().apply {
        items = FXCollections.observableArrayList("Aussendienst", "Admin", "Planung")
        promptText = "Abteilung asuwählen..."
        prefHeight = 30.0
        prefWidth = 300.0
    }
    val educationFiled = ComboBox<String>().apply {
        items = FXCollections.observableArrayList("Lehrling", "EFZ", "dipl. Pflegefachfrau HF")
        promptText = "Ausbildung auswählen..."
        prefHeight = 30.0
        prefWidth = 300.0
    }

    fun build(onClose: () -> Unit): Node =
        VBox(12.0).apply {
            padding = Insets(20.0)
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
                Label("Mitarbeiter suchen"),
                VBox().apply {
                    children.addAll(
                    HBox().apply {
                        children.addAll(
                            createComboBox("Abteilung:", departmentField), //TODO Erweitern mit Liste
                            createComboBox("Ausbildung:", educationFiled) //TODO Erweitern mit Liste
                        )
                    },
                    HBox().apply {
                        children.addAll(
                            createButton("Filter\neinsetzen").apply { setOnAction { filtern()} }, //TODO Funktionen einfügen
                            createButton("Abbrechen").apply { setOnAction { onClose() } } //TODO Funktionen einfügen
                        )
                        alignment = Pos.CENTER
                        padding = Insets(20.0)
                        spacing = 100.0
                    }
                    )

                }
            )
        }
}

fun filtern(){
    //TODO Filter Funktion erweitern
    //TODO Anzeig Mitarbeiter auflistung mit Liste
}

private fun createComboBox(labelText: String, choise: ComboBox<String>) = VBox(6.0).apply {
    padding = Insets(20.0)
    children.addAll(Label(labelText), choise)
    }

private fun createButton(text: String) = Button(text).apply {
    prefHeight = BTN_HEIGHT
    prefWidth = BTN_WIDTH
    textAlignment = TextAlignment.CENTER
    alignment = Pos.CENTER
    isFocusTraversable = false
    }



