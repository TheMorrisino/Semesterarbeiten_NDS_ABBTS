// Autor  Pedro

package ressourcix.gui.components

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

class PopUpLayer {

    private val dim = Region().apply {
        style = "-fx-background-color: rgba(0,0,0,0.35);"
        isVisible = false
        isMouseTransparent = false
        isManaged = true
    }

    private val popupHost = StackPane().apply {
        isVisible = false
        isMouseTransparent = false
        isManaged = true
        alignment = Pos.CENTER
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
    }

    fun wrap(content: Node, onDimClick: () -> Unit): StackPane =
        StackPane().apply {
            children.addAll(content, dim, popupHost)
            StackPane.setAlignment(dim, Pos.CENTER)
            StackPane.setAlignment(popupHost, Pos.CENTER)
            dim.setOnMouseClicked { onDimClick() }
        }

    fun show(popupContent: Node) {
        popupHost.children.setAll(popupContent)
        dim.isVisible = true
        popupHost.isVisible = true
        dim.toFront()
        popupHost.toFront()
    }

    fun close() {
        popupHost.children.clear()
        dim.isVisible = false
        popupHost.isVisible = false
    }
}
