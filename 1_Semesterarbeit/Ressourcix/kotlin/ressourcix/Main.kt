package ressourcix

import javafx.application.Application
import ressourcix.app.app
import gui.StartAnimation
import ressourcix.gui.GuiBorderPane
import ressourcix.logger.logger

fun main() {
    logger.setLevel(logger.Level.INFO)
    logger.info("=" .repeat(50))
    logger.info("Ressourcix wird gestartet...")
    logger.info("=" .repeat(50))


    Thread {
        try {
            logger.info("App-Logic-Thread gestartet")
            app.run()

        } catch (e: Exception) {
            logger.fatal("Kritischer Fehler in App-Logic", e)
        }
    }.apply {
        isDaemon = true
        name = "App-Logic-Thread"
    }.start()

    logger.debug("Alle Threads gestartet, starte JavaFX...")

    try {
        logger.info("Starte Ressourcix...")

        /* Wenn die APP ohne StartAnimation Sarten soll */
//          Application.launch(GuiBorderPane::class.java)

            Application.launch(StartAnimation::class.java)

    } catch (e: Exception) {
        logger.fatal("Fehler beim Starten der GUI", e)
    }
}