package ressourcix

//import Graphical
import javafx.application.Application
import ressourcix.app.app
import ressourcix.gui.GuiBorderPane
import ressourcix.gui.pages.calenderView
import ressourcix.gui.pages.dashboardView

fun main() {


    // App-Logik parallel starten
    Thread {
        (app.run())
    }.apply {
        isDaemon = true
        name = "App-Logic-Thread"
    }.start()



    // UI-Update-Thread
    Thread {
        // Kurz warten, bis die GUI initialisiert ist
        Thread.sleep(2000)

        while (true) {
            try {

                Thread.sleep(1000)
                calenderView.refreshVacations()
                dashboardView.updateBarChart()

            } catch (e: InterruptedException) {
                // Thread wurde unterbrochen, beenden
                break
            } catch (e: Exception) {
                println("Fehler beim UI-Update: ${e.message}")
                e.printStackTrace()
            }
        }
    }.apply {
        isDaemon = true
        name = "UI-Update-Thread"
    }.start()

    // JavaFX Application starten (blockiert bis Fenster geschlossen wird)
    Application.launch(GuiBorderPane::class.java)
}