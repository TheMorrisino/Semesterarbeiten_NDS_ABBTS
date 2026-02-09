package ressourcix.gui

//import Graphical
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import ressourcix.app.app.jasonFileAktiv
import ressourcix.essential.jsonWriter
import ressourcix.gui.navigation.*
import ressourcix.gui.pages.dashboardView
import ressourcix.logger.logger
import java.util.*
import kotlin.system.exitProcess

class GuiBorderPane : Application() {
    override fun start(stage: Stage) {

        val pathIcon = "/Ressourcix_Icon_OhneB2.png"
        val stream = javaClass.getResourceAsStream(pathIcon)

        val root = BorderPane().apply {
            top = topNavigationBar.getView()
            bottom = bottomBar.getView()
        }

        val router = NavigationsController(root)
        topNavigationBar.bind(router)
        router.navigate(Route.DASHBOARD)

        stage.apply {
            scene = Scene(root,1300.0,800.0)
            minHeight = 700.0
            minWidth = 700.0
            title = "Ressourcix"

            if (stream == null) {
                println("Das Bild wurde nicht gefunden unter: $pathIcon")
//                println("Arbeitsverzeichnis: " + System.getProperty("user.dir"))
            } else {
                val appIcon = Image(stream)
                stage.icons.add(appIcon)
            }
            setResizable(true)
            setOnCloseRequest {  event ->
                event.consume()
                exit() }
            show()


        }
    }

    val btnSave   = ButtonType.YES
    val btNotSave = ButtonType.NO
    val btnCancel = ButtonType.CANCEL

    fun exit() {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Ressourcix beenden"
            headerText = "Möchten Sie vor dem Schliessen speichern?"
            contentText = "Nicht gespeicherte Daten gehen verloren."

            buttonTypes.setAll(btnSave,btNotSave,btnCancel)



        }
        val pathIcon = "/Ressourcix_Icon_OhneB2.png"
        val stage = alert.dialogPane.scene.window as Stage
        stage.icons.add(Image(pathIcon))




        val result: Optional<ButtonType> = alert.showAndWait()

        when (result.orElse(btnCancel)) {
            btnSave -> {
                try {
                    if (jasonFileAktiv) {
                        jsonWriter.write()
                        logger.info("Daten erfolgreich gespeichert")
                        logger.info("=" .repeat(50))
                        logger.info("Ressourcix beendet")
                        logger.info("=" .repeat(50))
                        exitProcess(0)

                    } else {
                        logger.debug("JSON-Export ist deaktiviert.")

                    }
                } catch (e: Exception) {
                    logger.fatal("Fehler beim Speichern", e)
                    e.printStackTrace()
                }
            }

            btNotSave -> {
                logger.warn("Daten wurden beim Beenden NICHT gespeichert")
                logger.info("=" .repeat(50))
                logger.info("Ressourcix beendet")
                logger.info("=" .repeat(50))
                exitProcess(0)

            }
            btnCancel -> {
                logger.info("Ressourcix Beenden abgebrochen.")
            }
        }


    }

}
