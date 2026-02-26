// Autor: Pedro Santos

package ressourcix.gui.util

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment

fun createTfl(text: String, prompt: String): TextField =
    TextField(text).apply {
        promptText = prompt
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
        isFocusTraversable = false
    }

fun createButton(text: String): Button =
    Button(text).apply {
        prefHeight = BTN_HEIGHT
        prefWidth = BTN_WIDTH
        textAlignment = TextAlignment.CENTER
        alignment = Pos.CENTER
        isFocusTraversable = false
        style = "-fx-font-weight: bold;"
    }

fun createDataBox(labelText: String, field: TextField): VBox =
    VBox(6.0).apply {
        children.addAll(
            Label(labelText).apply { style = "-fx-font-weight: bold;" },
            field
        )
    }

fun <T> createLabeledComboBox(labelText: String, field: ComboBox<T>): VBox =
    VBox(6.0).apply {
        field.prefHeight = TFL_HEIGHT
        field.prefWidth = TFL_WIDTH

        children.addAll(
            Label(labelText).apply { style = "-fx-font-weight: bold;" },
            field
        )
    }