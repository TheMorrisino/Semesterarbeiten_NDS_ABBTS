package ressourcix.gui

import javafx.animation.*
import javafx.scene.Scene
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Glow
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.CycleMethod
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

    private val letters = mutableListOf<Text>()
    private val letterTexts = "RESSOURCIX".toCharArray()  //

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
        createLetters()
        startAnimation()
    }

    private fun setupStage() {
        root.apply {
            style = "-fx-background-color: linear-gradient(to bottom, #c0c0c0, #d0d0d0);"
            prefWidth = 800.0
            prefHeight = 600.0
        }
        stage.apply {
            initStyle(StageStyle.UNDECORATED)
            scene = Scene(root, 800.0, 400.0)
            isResizable = false
            centerOnScreen()
        }
    }

    private fun createLetters() {
        val startX = -290.0
        val y = 0.0          // passend zu translateY = 0 des Stick‑Man‑Groups (jetzt weg)
        val spacing = 62.0

        letterTexts.forEachIndexed { index, char ->
            val letter = Text(char.toString()).apply {
                font = Font.font("Arial", FontWeight.EXTRA_BOLD, 80.0)
                fill = gradient
                opacity = 0.0
                effect = DropShadow().apply {
                    color = Color.rgb(255, 140, 0, 0.8)
                    radius = 10.0
                    spread = 0.5
                }
                translateX = startX + (index * spacing)
                translateY = y
            }
            letters.add(letter)
        }
    }

    private fun startAnimation() {
        val mainTimeline = Timeline()
        var currentTime = 0.0

        letterTexts.forEachIndexed { index, _ ->
            val letter = letters[index]

            // POP‑Effekt
            mainTimeline.keyFrames.addAll(
                KeyFrame(Duration.seconds(currentTime),
                    KeyValue(letter.opacityProperty(), 0.0),
                    KeyValue(letter.scaleXProperty(), 0.5),
                    KeyValue(letter.scaleYProperty(), 0.5)
                ),
                KeyFrame(Duration.seconds(currentTime + 0.15),
                    KeyValue(letter.opacityProperty(), 1.0),
                    KeyValue(letter.scaleXProperty(), 1.3),
                    KeyValue(letter.scaleYProperty(), 1.3),
                    KeyValue(letter.rotateProperty(), 5.0)
                ),
                KeyFrame(Duration.seconds(currentTime + 0.3),
                    KeyValue(letter.scaleXProperty(), 1.0),
                    KeyValue(letter.scaleYProperty(), 1.0),
                    KeyValue(letter.rotateProperty(), 0.0)
                )
            )

            // Glow‑Effekt
            mainTimeline.keyFrames.add(
                KeyFrame(Duration.seconds(currentTime + 0.15), {
                    val glow = Glow(0.8)
                    letter.effect = glow
                    Timeline(
                        KeyFrame(Duration.seconds(0.5),
                            KeyValue(glow.levelProperty(), 0.0)
                        )
                    ).play()
                })
            )
            currentTime += 0.5
        }

        // kurze Pause nach allen Buchstaben
        mainTimeline.keyFrames.add(KeyFrame(Duration.seconds(currentTime + 0.5)))
        currentTime += 0.5

        // Fade‑Out und Abschluss
        mainTimeline.setOnFinished {
            fadeOut()
        }

        root.children.addAll(letters)
        mainTimeline.play()
    }

    private fun fadeOut() {
        val fadeTimeline = Timeline(
            KeyFrame(Duration.seconds(0.5),
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