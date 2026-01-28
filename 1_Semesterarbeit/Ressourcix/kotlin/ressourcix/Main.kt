//package ressourcix
//
//import Graphical
//import javafx.application.Application
//import ressourcix.app.App
//import ressourcix.gui.GuiBorderPane
//import ressourcix.gui.pages.calenderView
//import ressourcix.gui.pages.dashboardView
//import javafx.application.Platform
//
//fun main() {
//    val app: Graphical = App()
//    // App-Logik parallel starten
//    Thread {
//        (app as App).run()
//    }.apply { isDaemon = true }.start()
//
//    Thread {
//        while (true){
//            GuiBorderPane.graphical = app
//            Thread.sleep(1000)
//            calenderView.update()
//            dashboardView.updateBarChart()
//         }
//    }.apply { isDaemon = true }.start()
//
//    Application.launch(GuiBorderPane::class.java)
//
//}

package ressourcix

import Graphical
import javafx.application.Application
import ressourcix.app.App
import ressourcix.gui.GuiBorderPane
import ressourcix.gui.pages.calenderView
import ressourcix.gui.pages.dashboardView

fun main() {
    val app: Graphical = App()



    // App-Logik parallel starten
    Thread {
        (app as App).run()
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
                GuiBorderPane.graphical = app
                Thread.sleep(1000)
                calenderView.update()
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