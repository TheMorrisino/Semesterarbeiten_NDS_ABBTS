package ressourcix.gui.pages

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

object parameterview: BorderPane() {

    val minEmployeeTfl  = createTfl("","")
    val minStudentsTfl  = createTfl("","")
    val minManagerTfl  = createTfl("","")

    val vacationsBlockTfl = createTfl("","")
    val vacationsSchoolBlockTfl = createTfl("","")

    val vacationsUntil25Tfl = createTfl("","")
    val vacationBy25Tfl = createTfl("","")
    val vacationBy45Tfl = createTfl("","")

    private val dim = Region().apply {
        style = "-fx-background-color: rgba(0,0,0,0.35);"
        isVisible = false
        isMouseTransparent = false
        isManaged = true
        setOnMouseClicked { closePopup() }
    }

    private val popupHost = StackPane().apply {
        isVisible = false
        isMouseTransparent = false
        isManaged = true
        alignment = Pos.CENTER
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE

    }

    private val mainContent = BorderPane().apply {
        padding = Insets(10.0)
    }

    private val centerStack = StackPane().apply {
        children.addAll(mainContent, dim, popupHost)
        StackPane.setAlignment(dim, Pos.CENTER)
        StackPane.setAlignment(popupHost, Pos.CENTER)
    }

    private val vacationsParametersBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val minEmployee = createDataBox("Min. Anzahl Mitarbeiter",minEmployeeTfl)
        val minStudents = createDataBox("Min. Anzahl Lehrlinge",minStudentsTfl)
        val minManage = createDataBox("Min. Anzahl Leiter", minManagerTfl)

        children.addAll(minEmployee,minStudents,minManage)
    }

    private val vacationsBlockerBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val vacationsBlockDataB = createDataBox("Ferien Blocker",vacationsBlockTfl)
        val vacationsSchoolBlockDataB = createDataBox("Ferien Schule", vacationsSchoolBlockTfl)

        children.addAll(vacationsBlockDataB,vacationsSchoolBlockDataB)

    }

    private val maxVacationsBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val minEmployee = createDataBox("Min. Anzahl Mitarbeiter",minEmployeeTfl)
        val minStudents = createDataBox("Min. Anzahl Lehrlinge",minStudentsTfl)
        val minManage = createDataBox("Min. Anzahl Leiter", minManagerTfl)

        children.addAll(minEmployee,minStudents,minManage)

    }

    init {
        mainContent.top = vacationsParametersBar
        mainContent.center = vacationsBlockerBar
        center = centerStack

    }


    fun showPopup(popupContent: Node) {
        popupHost.children.setAll(popupContent)
        dim.isVisible = true
        popupHost.isVisible = true
        dim.toFront()
        popupHost.toFront()
    }

    fun closePopup() {
        popupHost.children.clear()
        dim.isVisible = false
        popupHost.isVisible = false
    }

    private fun createTfl(text: String,prompt: String): TextField =
        TextField(text).apply {
            promptText = prompt
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
        }

    private fun createButton(text: String): Button =
        Button(text).apply {
            prefHeight = BTN_HEIGHT
            prefWidth = BTN_WIDTH
            textAlignment = TextAlignment.CENTER
            alignment = Pos.CENTER
            isFocusTraversable = false
            style = " -fx-font-weight: bold;"
        }

    private fun createDataBox(labelText: String, field: TextField): VBox =
        VBox(6.0).apply {
            children.addAll(Label(labelText).apply {
                style = " -fx-font-weight: bold;"
            }, field)
        }

    private fun <T> createComboBox(labelText: String, field: ComboBox<T>): VBox =
        VBox(6.0).apply {
            children.addAll(
                Label(labelText).apply { style = "-fx-font-weight: bold;" },
                field
            )
        }

}