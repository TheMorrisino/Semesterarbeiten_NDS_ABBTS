// Autor: Pedro Santos
//        Morris Meier

package ressourcix.gui

import javafx.animation.Animation
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

    private object Config {
        const val CLOCK_FORMAT = "dd.MM.yyyy HH:mm:ss"
        const val STATUS_FORMAT = "HH:mm:ss"
        const val DEFAULT_STATUS = "Bereit"
        const val SPACING = 10.0
        const val PADDING = 5.0
        const val CLOCK_INTERVAL_SECONDS = 1.0

        const val STATUS_STYLE = "-fx-background-color: #f5f5f5; -fx-opacity: 1.0;"
        const val CLOCK_STYLE = "-fx-text-fill: black;"
        const val BAR_STYLE = "-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;"
    }

    private val statusField: TextField by lazy { createStatusField() }
    private val clockLabel: Label by lazy { createClockLabel() }
    private val clockTimeline: Timeline by lazy { createClockTimeline() }

    private val clockFormatter = DateTimeFormatter.ofPattern(Config.CLOCK_FORMAT)
    private val statusFormatter = DateTimeFormatter.ofPattern(Config.STATUS_FORMAT)


    init {
        updateClock()
        subscribeToLogUpdates()
    }


    fun getView(): HBox = createBar()

    fun start() {
        clockTimeline.play()
    }

    fun stop() {
        clockTimeline.stop()
    }


    private fun subscribeToLogUpdates() {
        logger.addLogChangeListener { entry ->
            val timestamp = entry.timestamp.format(statusFormatter)
            val message = "$timestamp - ${entry.message}"
            updateStatus(message)
        }
    }

    private fun updateStatus(message: String) {
        Platform.runLater {
            statusField.text = message
        }
    }


    private fun createClockTimeline() = Timeline(
        KeyFrame(Duration.seconds(Config.CLOCK_INTERVAL_SECONDS), EventHandler { updateClock() })
    ).apply {
        cycleCount = Animation.INDEFINITE
    }

    private fun updateClock() {
        Platform.runLater {
            clockLabel.text = LocalDateTime.now().format(clockFormatter)
        }
    }


    private fun createStatusField() = TextField(Config.DEFAULT_STATUS).apply {
        isDisable = true
        isEditable = false
        style = Config.STATUS_STYLE
    }

    private fun createClockLabel() = Label().apply {
        style = Config.CLOCK_STYLE
    }

    private fun createBar() = HBox(Config.SPACING).apply {
        padding = Insets(Config.PADDING)
        alignment = Pos.CENTER_LEFT
        style = Config.BAR_STYLE

        HBox.setHgrow(statusField, Priority.ALWAYS)
        children.addAll(statusField, createSpacer(), clockLabel)
    }

    private fun createSpacer() = Region().apply {
        HBox.setHgrow(this, Priority.ALWAYS)
    }
}