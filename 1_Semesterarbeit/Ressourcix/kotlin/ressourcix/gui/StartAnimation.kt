package gui

import javafx.application.Application
import javafx.stage.Stage
import ressourcix.gui.GuiBorderPane
import ressourcix.logger.logger

class StartAnimation : Application() {

    override fun start(primaryStage: Stage) {
        val splash = SplashScreenAdvanced {
            val gui = GuiBorderPane()
            gui.start(primaryStage)
        }
        splash.show()
    }

}