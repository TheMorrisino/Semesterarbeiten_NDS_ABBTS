package ressourcix.gui.util

import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter

fun installBirthdayField(field: TextField) {
    field.textFormatter = TextFormatter<String> { change ->
        if (!change.isContentChange) return@TextFormatter change

        val old = change.controlText

        if (change.text.isEmpty() && change.rangeStart < change.rangeEnd) {
            val deleted = old.substring(change.rangeStart, change.rangeEnd)
            if (deleted == ".") {
                val s = change.rangeStart
                val e = change.rangeEnd
                if (s > 0) {
                    change.setRange(s - 1, e)
                } else if (e < old.length) {
                    change.setRange(s, e + 1)
                }
            }
        }

        val newText = change.controlNewText
        if (newText.isEmpty()) return@TextFormatter change

        if (!newText.all { it.isDigit() || it == '.' }) return@TextFormatter null
        if (newText.count { it.isDigit() } > 8) return@TextFormatter null

        change
    }

    var updating = false

    field.textProperty().addListener { _, _, value ->
        if (updating) return@addListener

        val caret = field.caretPosition
        val digitsBeforeCaret = value.take(caret).count { it.isDigit() }

        val digits = value.filter { it.isDigit() }.take(8)
        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                append(c)
                if (i == 1 || i == 3) append('.')
            }
        }

        if (value == formatted) return@addListener

        updating = true
        field.text = formatted

        var newCaret = digitsBeforeCaret
        if (digitsBeforeCaret >= 2) newCaret += 1
        if (digitsBeforeCaret >= 4) newCaret += 1

        field.positionCaret(newCaret.coerceIn(0, formatted.length))
        updating = false
    }
}
