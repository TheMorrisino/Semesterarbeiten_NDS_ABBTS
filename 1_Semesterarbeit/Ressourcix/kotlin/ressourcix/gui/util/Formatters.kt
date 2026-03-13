// Autor Pedro

package ressourcix.gui.util

import javafx.scene.control.TextFormatter

fun positiveIntNoZeroFormatter(maxDigits: Int, maxValue: Int): TextFormatter<String> =
    TextFormatter { change ->
        val newText = change.controlNewText
        val ok = newText.isEmpty() || (
                newText.matches(Regex("[1-9][0-9]*")) &&
                        newText.length <= maxDigits &&
                        newText.toInt() <= maxValue
                )
        if (ok) change else null
    }

fun lettersMaxFormatter(maxLetters: Int, bigLetters: Boolean): TextFormatter<String> {
    val allowedChars = if (bigLetters) "A-ZÄÖÜ \\-" else "A-Za-zÄÖÜäöü \\-"
    val pattern = Regex("^[$allowedChars]{0,$maxLetters}$")

    return TextFormatter { change ->
        if (!change.isContentChange) return@TextFormatter change
        if (bigLetters && change.text.isNotEmpty()) change.text = change.text.uppercase()

        val newText = change.controlNewText
        if (pattern.matches(newText)) change else null
    }
}