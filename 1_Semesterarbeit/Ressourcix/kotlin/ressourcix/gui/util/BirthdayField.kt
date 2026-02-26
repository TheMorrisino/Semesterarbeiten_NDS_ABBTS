// Autor: Pedro Santos

package ressourcix.gui.util

import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import java.time.LocalDate

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

        val digits = newText.filter { it.isDigit() }
        if (digits.length > 8) return@TextFormatter null

        if (digits.length >= 2) {
            val day = digits.substring(0, 2).toIntOrNull() ?: return@TextFormatter null
            if (day !in 1..31) return@TextFormatter null
        }

        if (digits.length >= 4) {
            val month = digits.substring(2, 4).toIntOrNull() ?: return@TextFormatter null
            if (month !in 1..12) return@TextFormatter null
        }

        if (digits.length == 8) {
            val year = digits.substring(4, 8).toIntOrNull() ?: return@TextFormatter null
            val currentYear = LocalDate.now().year
            if (year > currentYear) return@TextFormatter null

            val day = digits.substring(0, 2).toInt()
            val month = digits.substring(2, 4).toInt()
            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                return@TextFormatter null
            }
        }

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
