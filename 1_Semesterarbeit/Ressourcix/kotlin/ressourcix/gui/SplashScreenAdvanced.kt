// Autor GPT

package gui

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.Scene
import javafx.scene.effect.DropShadow
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration

class SplashScreenAdvanced(private val onFinished: () -> Unit) {
    private val stage = Stage()
    private val root = StackPane()
    private lateinit var title: Text
    private val gradient = LinearGradient(
        0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
        Stop(0.0, Color.rgb(139, 0, 0)),
        Stop(0.3, Color.rgb(178, 34, 34)),
        Stop(0.5, Color.rgb(138, 43, 226)),
        Stop(0.7, Color.rgb(186, 85, 211)),
        Stop(1.0, Color.rgb(255, 140, 0))
    )

    init {
        setupStage()
        createTitle()
        startAnimation()
    }

    private fun setupStage() {
        root.apply {
            prefWidth = 800.0
            prefHeight = 600.0
            background = null

        }

        stage.apply {
            initStyle(StageStyle.TRANSPARENT)
            scene = Scene(root, 800.0, 400.0, Color.TRANSPARENT)
            isResizable = false
            centerOnScreen()
        }

    }

    private fun createTitle() {
        title = Text("RESSOURCIX").apply {
            font = Font.font("Arial", FontWeight.EXTRA_BOLD, 80.0)
            fill = gradient
            opacity = 0.0                     // startet unsichtbar
            effect = DropShadow().apply {
                color = Color.rgb(255, 140, 0, 0.8)
                radius = 10.0
                spread = 0.5
            }
        }
    }

    private fun startAnimation() {
        val mainTimeline = Timeline()
        mainTimeline.keyFrames.addAll(

            KeyFrame(
                Duration.seconds(0.0),
                KeyValue(title.opacityProperty(), 0.0),
                KeyValue(title.scaleXProperty(), 0.5),
                KeyValue(title.scaleYProperty(), 0.5)
            ),

            KeyFrame(
                Duration.seconds(0.15),
                KeyValue(title.opacityProperty(), 1.0),
                KeyValue(title.scaleXProperty(), 1.3),
                KeyValue(title.scaleYProperty(), 1.3),
                KeyValue(title.rotateProperty(), 5.0)
            ),

            KeyFrame(
                Duration.seconds(0.3),
                KeyValue(title.scaleXProperty(), 1.0),
                KeyValue(title.scaleYProperty(), 1.0),
                KeyValue(title.rotateProperty(), 0.0)
            )
        )

        mainTimeline.keyFrames.add(KeyFrame(Duration.seconds(3.0)))

        mainTimeline.setOnFinished { fadeOut() }
        root.children.add(title)
        mainTimeline.play()
    }

    private fun fadeOut() {
        val fadeTimeline = Timeline(
            KeyFrame(
                Duration.seconds(0.8),

                KeyValue(root.opacityProperty(), 0.0)
            )
        )
        fadeTimeline.setOnFinished {
            close()
            onFinished()
        }
        fadeTimeline.play()
    }

    fun show() = stage.show()
    fun close() = stage.close()
}