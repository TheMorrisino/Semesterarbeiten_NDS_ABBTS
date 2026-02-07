package ressourcix

import javafx.application.Application
import ressourcix.app.app
import ressourcix.gui.GuiBorderPane
import ressourcix.gui.pages.calenderView
import ressourcix.gui.pages.dashboardView
import ressourcix.logger.logger

fun main() {

    // Logger konfigurieren
    logger.setLevel(logger.Level.INFO)
    logger.info("=" .repeat(50))
    logger.info("Ressourcix wird gestartet...")
    logger.info("=" .repeat(50))

    // App-Logik parallel starten
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

    // UI-Update-Thread (nur für calenderView)
//    Thread {
//        logger.info("UI-Update-Thread gestartet")
//
//        // Kurz warten, bis die GUI initialisiert ist
//        Thread.sleep(2000)
//
//        while (true) {
//            try {
//                Thread.sleep(1000)
//
//
//            } catch (e: InterruptedException) {
//                logger.info("UI-Update-Thread wurde beendet")
//                break
//            } catch (e: Exception) {
//                logger.error("Fehler beim UI-Update", e)
//            }
//        }
//    }.apply {
//        isDaemon = true
//        name = "UI-Update-Thread"
//    }.start()


    Thread {
        var lastMessage = ""

        while (true) {
            try {
                Thread.sleep(500)  // Nur alle 500ms prüfen (spart CPU!)

                val currentMessage = logger.getLastLogMessage() ?: "Bereit"

                // Nur loggen wenn sich was geändert hat
                if (currentMessage != lastMessage) {
                    lastMessage = currentMessage
                    // Status hat sich geändert - GUI wird automatisch via Property upgedatet
                }

            } catch (e: InterruptedException) {
                logger.info("Status-Update-Thread wurde beendet")
                break
            } catch (e: Exception) {
                logger.error("Fehler im Status-Update", e)
            }
        }
    }.apply {
        isDaemon = true
        name = "Status-Update-Thread"
    }.start()

    logger.debug("Alle Threads gestartet, starte JavaFX...")


    // JavaFX Application starten (blockiert bis Fenster geschlossen wird)
    try {
        logger.info("Starte Ressourcix...")
        Application.launch(GuiBorderPane::class.java)



    } catch (e: Exception) {
        logger.fatal("Fehler beim Starten der GUI", e)
    }

    logger.info("=" .repeat(50))
    logger.info("Ressourcix beendet")
    logger.info("=" .repeat(50))
}