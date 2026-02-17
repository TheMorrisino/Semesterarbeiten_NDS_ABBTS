package ressourcix.gui


import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import ressourcix.app.app.jasonFileAktiv
import ressourcix.essential.jsonWriter
import ressourcix.gui.navigation.*
import ressourcix.logger.logger
import java.util.*
import kotlin.system.exitProcess

class GuiBorderPane : Application() {
    override fun start(stage: Stage) {

        val baseW = 1300.0
        val baseH = 800.0

        val pathIcon = "/Ressourcix_Icon_OhneB2.png"
        val stream = javaClass.getResourceAsStream(pathIcon)

        val root = BorderPane().apply {
            top = topNavigationBar.getView()
            bottom = bottomBar.getView()
            prefWidth = baseW
            prefHeight = baseH
        }

        val router = NavigationsController(root)
        topNavigationBar.bind(router)
        router.navigate(Route.DASHBOARD)

        val contentGroup = Group(root)
        val outer = StackPane(contentGroup)

        val scene = Scene(outer, baseW, baseH)

        fun updateScale() {
            val sx = scene.width / baseW
            val sy = scene.height / baseH
            contentGroup.scaleX = sx
            contentGroup.scaleY = sy
        }

        scene.widthProperty().addListener { _, _, _ -> updateScale() }
        scene.heightProperty().addListener { _, _, _ -> updateScale() }
        updateScale()

        stage.apply {
            this.scene = scene
            minWidth = 1100.0
            minHeight = 600.0
            title = "Ressourcix"

            if (stream == null) {
                println("Das Bild wurde nicht gefunden unter: $pathIcon")
            } else {
                val appIcon = Image(stream)
                stage.icons.add(appIcon)
            }
            setResizable(true)
            setOnCloseRequest {  event ->
                event.consume()
                exit() }
            bottomBar.start()
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
