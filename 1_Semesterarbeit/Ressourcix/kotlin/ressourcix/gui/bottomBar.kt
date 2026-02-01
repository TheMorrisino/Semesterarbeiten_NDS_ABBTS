package ressourcix.gui

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.util.Duration
import ressourcix.logger.logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object bottomBar {

    // Status-TextField (links)
    private val status = TextField().apply {
        isDisable = false
        isEditable = false
        style = "-fx-background-color: #f5f5f5; -fx-opacity: 1.0;"
        text = "Bereit"
    }

    // Uhr-Label (rechts)
    private val clockLabel = Label().apply {
        style = "-fx-text-fill: gray;"
    }

    // Formatter
    private val timeFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    // Cache für letzte Log-Message
    private var lastMessage = ""

    private val bar = HBox(10.0).apply {
        padding = Insets(5.0)
        alignment = Pos.CENTER_LEFT

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        children.addAll(status, spacer, clockLabel)

        HBox.setHgrow(status, Priority.ALWAYS)
        style = "-fx-background-color: #f5f5f5; -fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;"
    }


    private val statusUpdateThread = Thread {
        while (true) {
            try {
                // Nur alle 500ms prüfen (spart CPU!)
                Thread.sleep(500)

                val currentMessage = logger.getLastLogMessage() ?: "Bereit"

                // Nur updaten wenn sich was geändert hat
                if (currentMessage != lastMessage) {
                    lastMessage = currentMessage

                    val lastEntry = logger.getLastLogEntry()

                    // UI-Update im JavaFX-Thread
                    Platform.runLater {
                        updateStatus(currentMessage, lastEntry?.level)
                    }
                }


            } catch (e: InterruptedException) {
                break // Thread beenden
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }.apply {
        isDaemon = true
        name = "BottomBar-Status-Updater"
        start()
    }

    private fun updateClock() {
        clockLabel.text = LocalDateTime.now().format(timeFmt)
    }

    private fun updateStatus(message: String, level: logger.Level?) {
        status.text = message

        // Farbe basierend auf Log-Level
        status.style = when (level) {
            logger.Level.ERROR, logger.Level.FATAL ->
                "-fx-background-color: #ffe6e6; -fx-text-fill: red; -fx-font-weight: bold; -fx-opacity: 1.0;"
            logger.Level.WARN ->
                "-fx-background-color: #fff4e6; -fx-text-fill: orange; -fx-font-weight: bold; -fx-opacity: 1.0;"
//           logger.Level.INFO ->
////                "-fx-background-color: #e6f3ff; -fx-text-fill: blue; -fx-opacity: 1.0;"
            logger.Level.DEBUG ->
                "-fx-background-color: #f5f5f5; -fx-text-fill: gray; -fx-opacity: 1.0;"
            else ->
                "-fx-background-color: #f5f5f5; -fx-text-fill: black; -fx-opacity: 1.0;"
        }
    }
    fun getView(): HBox = bar
}