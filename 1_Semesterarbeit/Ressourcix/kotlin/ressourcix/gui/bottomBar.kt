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

    // ====================================================================================================
    // CONSTANTS
    // ====================================================================================================
    private const val TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
    private const val DEFAULT_STATUS = "Bereit"


    // ====================================================================================================
    // UI COMPONENTS
    // ====================================================================================================
    private val statusField = createStatusField()
    private val clockLabel = createClockLabel()
    private val bar = createBar()

    // ====================================================================================================
    // STATE
    // ====================================================================================================
    private var lastMessage = ""

    // ====================================================================================================
    // INITIALIZATION
    // ====================================================================================================
    init {
        startStatusUpdater()
    }

    // ====================================================================================================
    // UI CREATION
    // ====================================================================================================
    private fun createStatusField() = TextField().apply {
        isDisable = true
        isEditable = false
        text = DEFAULT_STATUS
        style = "-fx-background-color: #f5f5f5; -fx-opacity: 1.0;"
    }

    private fun createClockLabel() = Label().apply {
        style = "-fx-text-fill: black;"
    }

    private fun createBar() = HBox(10.0).apply {
        padding = Insets(5.0)
        alignment = Pos.CENTER_LEFT
        style = "-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;"

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }

        HBox.setHgrow(statusField, Priority.ALWAYS)
        children.addAll(statusField, spacer, clockLabel)
    }

    // ====================================================================================================
    // STATUS UPDATE
    // ====================================================================================================
    private fun startStatusUpdater() {
        Thread {
            while (true) {
                try {
                    updateStatusIfChanged()
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.apply {
            isDaemon = true
            name = "BottomBar-Status-Updater"
            start()
        }
    }

    private fun updateStatusIfChanged() {
        val currentMessage = logger.getLastLogMessageWithTimestamp() ?: DEFAULT_STATUS

        if (currentMessage != lastMessage) {
            lastMessage = currentMessage
            Platform.runLater {
                statusField.text = currentMessage
            }
        }
    }

    // ====================================================================================================
    // CLOCK UPDATE
    // ====================================================================================================
    val clockTimeline = Timeline().apply {
        keyFrames.add(KeyFrame(Duration.ZERO, EventHandler { updateClock() }))
        keyFrames.add(KeyFrame(Duration.seconds(1.0), EventHandler { updateClock() }))
        cycleCount = Timeline.INDEFINITE
        play()
    }

    private fun updateClock() {
        val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT)
        clockLabel.text = LocalDateTime.now().format(formatter)
    }

    // ====================================================================================================
    // GETTER
    // ====================================================================================================
    fun getView(): HBox = bar
}