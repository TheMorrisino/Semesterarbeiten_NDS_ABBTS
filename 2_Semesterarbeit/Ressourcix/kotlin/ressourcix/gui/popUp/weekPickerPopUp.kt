// Autor Pedro

package gui.popUp

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ressourcix.gui.util.*

private const val BOX_HEIGHT = 300.0
private const val BOX_WIDTH = 800.0

class weekPickerPopUp(private val label: String,
                      private val weeks: BooleanArray,
                      private val onTextChanged: (String) -> Unit,
                      private val onClose: () -> Unit) {

    fun build(): Node {
        val grid = GridPane().apply {
            hgap = 6.0
            vgap = 6.0
            padding = Insets(15.0)
        }

        val cols = 13
        for (w in 1..52) {
            val btn = ToggleButton("KW $w").apply {
                styleClass.add("btnWeekStyle")
                isSelected = weeks[w]
                prefWidth = 78.0
                setOnAction {
                    weeks[w] = isSelected
                    onTextChanged(formatWeeksAsRanges(weeks))
                }
            }
            val col = (w - 1) % cols
            val row = (w - 1) / cols
            grid.add(btn, col, row)
        }

        val clearBtn = createButton("Alles löschen").apply {
            setOnAction {
                for (w in 1..52) weeks[w] = false
                grid.children.filterIsInstance<ToggleButton>().forEach { it.isSelected = false }
                onTextChanged("")
            }
        }

        val closeBtn = createButton("Schliessen").apply {
            setOnAction { onClose() }
        }

        val functionBox = HBox(10.0, closeBtn, clearBtn ).apply {
            alignment = Pos.BOTTOM_CENTER
            padding = Insets(0.0, 15.0, 15.0, 15.0)
        }

        onTextChanged(formatWeeksAsRanges(weeks))

        return VBox(12.0).apply {
            padding = Insets(20.0)
            maxWidth = BOX_WIDTH
            maxHeight = BOX_HEIGHT
            style = """
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 4);
            """.trimIndent()
            children.addAll(Label(label).apply {
                style = "-fx-font-size: 18px; -fx-font-weight: bold;"
            },
                grid,functionBox)
        }
    }

    private fun formatWeeksAsRanges(weeks: BooleanArray): String {
        val ranges = mutableListOf<Pair<Int, Int>>()
        var i = 1
        while (i <= 52) {
            if (!weeks[i]) { i++; continue }
            val start = i
            var end = i
            while (end + 1 <= 52 && weeks[end + 1]) end++
            ranges += start to end
            i = end + 1
        }

        return ranges.joinToString(", ") { (s, e) ->
            if (s == e) "KW $s" else "KW $s-$e"
        }
    }

}
