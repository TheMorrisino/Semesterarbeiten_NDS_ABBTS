package ressourcix.gui

import Graphical
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class GuiBorderPane : Application() {

    companion object {
        // Muss in Main.kt gesetzt werden: GuiBorderPane.graphical = app
        lateinit var graphical: Graphical
    }

    private lateinit var root: BorderPane
    private lateinit var leftArea: VBox

    override fun start(stage: Stage) {
        root = BorderPane()
        leftArea = VBox(10.0).apply { prefWidth = 300.0 }

        // Links: Abkürzungen als ListView
        val abbreviations = FXCollections.observableArrayList(
            graphical.employees.map { it.abbreviationSting() } // <- ggf. anpassen!
        )
        val listView = ListView(abbreviations)
        leftArea.children.addAll(Label("Mitarbeitende"), listView)

        // Menü
        val menuBar = bar(
            menu("Datei",
                item("Neu") { neueDatei() },
                item("Öffnen") { oeffneDatei() },
                separator(),
                item("Beenden") { beenden() }
            ),
            menu("Ansicht",
                item("Left-Bereich ein/aus") { toggleLeftBereich() },
                separator(),
                item("Vollbild") { vollbild(stage) }
            ),
            menu("Hilfe",
                item("Über") { zeigUeber() }
            )
        )

        root.top = menuBar
        root.left = leftArea
        root.center = TextArea("Center-Bereich")

        stage.scene = Scene(root, 900.0, 600.0)
        stage.title = "BorderPane"
        stage.show()
    }

    // --- Menü-Helfer ---
    private fun bar(vararg elements: Menu) = MenuBar().apply { menus.addAll(elements) }
    private fun menu(text: String, vararg elements: MenuItem) = Menu(text).apply { items.addAll(elements) }
    private fun item(text: String, method: () -> Unit) = MenuItem(text).apply { setOnAction { method() } }
    private fun separator() = SeparatorMenuItem()

    // --- Aktionen ---
    private fun neueDatei() {
        println("Neue Datei erstellt")
    }

    private fun oeffneDatei() {
        println("Datei öffnen Dialog")
    }

    private fun beenden() {
        javafx.application.Platform.exit()
    }

    private fun toggleLeftBereich() {
        root.left = if (root.left == null) leftArea else null
    }

    private fun vollbild(stage: Stage) {
        stage.isFullScreen = !stage.isFullScreen
    }

    private fun zeigUeber() {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Über"
            headerText = "BorderPane Demo"
            contentText = "GUI mit Interface (Graphical) als Datenquelle"
        }.showAndWait()
    }


    //kdnasdbsak
}
