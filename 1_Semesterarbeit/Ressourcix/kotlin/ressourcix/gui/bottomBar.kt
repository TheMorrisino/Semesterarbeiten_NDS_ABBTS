package ressourcix.gui

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.util.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object bottomBar {
    private var status = TextField("Meldungen werden hier angezeigt").apply {
        setDisable(true)
        isEditable = false
    }

    private val clockLabel = Label().apply {
        setDisable(true)
    }
    private val timeFmt = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss")

    private val bar = HBox(10.0).apply {
        padding = Insets(5.0)
        alignment = Pos.CENTER_LEFT

        val spacer = Region().apply {
            HBox.setHgrow(this, Priority.ALWAYS)
        }
        children.addAll(status,clockLabel )

        HBox.setHgrow(status, Priority.ALWAYS)
        style = "-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;"
    }

    private val clockTimeline = Timeline().apply {
        keyFrames.add(KeyFrame(Duration.ZERO, EventHandler { updateClock() }))
        keyFrames.add(KeyFrame(Duration.seconds(1.0), EventHandler { updateClock() }))
        cycleCount = Timeline.INDEFINITE
        play()
    }

    private fun updateClock() {
        clockLabel.text = LocalDateTime.now().format(timeFmt)

    }

    fun getView(): HBox = bar
}