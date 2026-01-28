package ressourcix.gui

import Graphical
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import ressourcix.gui.navigation.*
import java.util.*
import kotlin.system.exitProcess

class GuiBorderPane : Application() {

    companion object {
        // Muss in Main.kt gesetzt werden: GuiBorderPane.graphical = app
        lateinit var graphical: Graphical
    }

    override fun start(stage: Stage) {

        val pathIcon = "/Ressourcix_Icon_OhneB2.png"
        val stream = javaClass.getResourceAsStream(pathIcon)


        val root = BorderPane().apply {
            top = TopNavigationBar.getView()
            bottom = bottomBar.getView()

        }

        val router = NavigationsController(root)
        TopNavigationBar.bind(router)
        router.navigate(Route.DASHBOARD)

        stage.apply {
            scene = Scene(root,500.0,500.0)
            title = "Ressourcix"

            if (stream == null) {
                println("Das Bild wurde nicht gefunden unter: $pathIcon")
//                println("Arbeitsverzeichnis: " + System.getProperty("user.dir"))
            } else {
                val appIcon = Image(stream)
                stage.icons.add(appIcon)
            }
            setResizable(true)
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
