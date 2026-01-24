package ressourcix.gui

import Graphical
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import ressourcix.gui.navigation.*
import java.util.Optional
import kotlin.system.exitProcess

class GuiBorderPane : Application() {

    companion object {
        // Muss in Main.kt gesetzt werden: GuiBorderPane.graphical = app
        lateinit var graphical: Graphical
    }

    override fun start(stage: Stage) {
        val root = BorderPane().apply {
            top = TopNavigationBar.getView()
            bottom = bottomBar.getView()
        }

        val router = NavigationsController(root)
        TopNavigationBar.bind(router)
        router.navigate(Route.DASHBOARD)

        stage.apply {
            scene = Scene(root, 1100.0, 700.0)
            title = "Ressourcix"
            setResizable(false)
            setOnCloseRequest { exit() }
            show()
        }
    }


    fun exit() {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Ressourcix beenden"
            headerText = "Möchten Sie Ressourcix wirklich beenden?"
            contentText = "Nicht gespeicherte Daten gehen verloren."
        }

        val result: Optional<ButtonType> = alert.showAndWait()

        if (result.isPresent && result.get() == ButtonType.OK) {

            exitProcess(0)
        }
    }
}
