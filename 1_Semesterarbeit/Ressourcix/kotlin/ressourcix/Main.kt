package ressourcix

import Graphical
import javafx.application.Application
import ressourcix.app.App
import ressourcix.gui.GuiBorderPane


fun main() {
    val app: Graphical = App()   // 👈 Interface-Typ!

    // GUI bekommt Interface, nicht konkrete Klasse
    GuiBorderPane.graphical = app

    // App-Logik parallel starten
    Thread {
        (app as App).run()
    }.apply { isDaemon = true }.start()

    Application.launch(GuiBorderPane::class.java)
}