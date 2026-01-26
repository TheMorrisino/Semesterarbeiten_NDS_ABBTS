package ressourcix

import Graphical
import javafx.application.Application
import ressourcix.app.App
import ressourcix.gui.GuiBorderPane
import ressourcix.gui.pages.calenderView

fun main() {
    val app: Graphical = App()
    // App-Logik parallel starten
    Thread {
        (app as App).run()
    }.apply { isDaemon = true }.start()

    Thread {
        while (true){
            GuiBorderPane.graphical = app
            Thread.sleep(1000)
            calenderView.update()
         }
    }.apply { isDaemon = true }.start()

    Application.launch(GuiBorderPane::class.java)

}