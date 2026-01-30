package ressourcix

import Graphical
import javafx.application.Application
import ressourcix.app.App
import ressourcix.gui.GuiBorderPane

fun main() {
    val app: Graphical = App

    GuiBorderPane.graphical = app

    Thread {
        (app as App).run()
    }.apply {
        isDaemon = true
        name = "App-Logic-Thread"
    }.start()

    Application.launch(GuiBorderPane::class.java)
}
