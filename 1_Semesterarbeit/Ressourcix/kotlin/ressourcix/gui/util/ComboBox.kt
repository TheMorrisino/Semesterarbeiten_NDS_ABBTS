package ressourcix.gui.util

import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell

fun <T> ComboBox<T>.promptWhenNull() {
    val p = promptText
    buttonCell = object : ListCell<T>() {
        override fun updateItem(item: T?, empty: Boolean) {
            super.updateItem(item, empty)
            text = if (empty || item == null) p else item.toString()
        }
    }
}
