//  Autor:        Pedro Santos

package ressourcix.gui.pages

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.scene.input.KeyEvent
import ressourcix.gui.popups.weekPickerPopUp

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

object parameterview: BorderPane() {

    private val weeksForBlocker = BooleanArray(53)
    private val weeksForSchool  = BooleanArray(53)

    private var isEditMode: Boolean = false

    val minEmployeeTfl  = createTfl("","")
    val minStudentsTfl  = createTfl("","")
    val minManagerTfl  = createTfl("","")

    val vacationsBlockTfl = createTfl("","Blockierte Wochen eintragen...").apply {
        lockToPopupOnly(this)
    }
    val vacationsSchoolBlockTfl = createTfl("","Schulferien eintragen").apply {
        lockToPopupOnly(this)
    }

    val vacationsUntil25Tfl = createTfl("","")
    val vacationFrom25Tfl = createTfl("","")
    val vacationFrom45Tfl = createTfl("","")

    private val parameterChangeBtn = createButton("Parameter \nändern").apply {
        setOnAction { setFieldsEditable(true) }
    }
    private val parameterSaveBtn = createButton("Parameter \nspeichern").apply {
        setOnAction{
            if (!isEditMode) return@setOnAction
            setFieldsEditable(false)
        }
    }
    private val parameterRestoreBtn = createButton("Parameter \nzurücksetzen").apply {
        setOnAction { setFieldsEditable(false) }
    }

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

    private val mainContent = VBox().apply {
        padding = Insets(50.0)
        alignment = Pos.CENTER
        spacing = 50.0
    }

    private val centerStack = StackPane().apply {
        children.addAll(mainContent, dim, popupHost)
        StackPane.setAlignment(dim, Pos.CENTER)
        StackPane.setAlignment(popupHost, Pos.CENTER)
    }

    private val minEmployeeBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.TOP_LEFT

        val minEmployee = createDataBox("Min. Anzahl Mitarbeiter",minEmployeeTfl)
        val minStudents = createDataBox("Min. Anzahl Lehrlinge",minStudentsTfl)
        val minManage = createDataBox("Min. Anzahl Leiter", minManagerTfl)

        children.addAll(minEmployee,minStudents,minManage)
    }

    private val vacationsBlockerBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER_LEFT

        val vacationsBlockDataB = createDataBox("Ferien Blocker",vacationsBlockTfl)
        val vacationsSchoolBlockDataB = createDataBox("Schulferien", vacationsSchoolBlockTfl)

        children.addAll(vacationsBlockDataB,vacationsSchoolBlockDataB)

    }

    private val maxVacationsBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.BOTTOM_LEFT

        val maxVacationUntil25DataB = createDataBox("Anzahl Ferien bis 25 Jahre alt",vacationsUntil25Tfl)
        val maxVacationFrom25DataB = createDataBox("Anzahl Ferien ab 25 Jahre alt",vacationFrom25Tfl)
        val maxVacationFrom45DataB = createDataBox("Anzahl Ferien ab 45 Jahre alt", vacationFrom45Tfl)

        children.addAll(maxVacationUntil25DataB,maxVacationFrom25DataB,maxVacationFrom45DataB)

    }

    private val functionBox = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.BOTTOM_CENTER
        spacing = 80.0

        children.addAll(parameterChangeBtn,parameterSaveBtn,parameterRestoreBtn)
    } //TODO Speichern und Zurücksetzen Funktion implementieren.
    //TODO Daten verknüpfen zum Speichern und Zurücksetzen
    // TODO logger funktionen implementieren
    // TODO TextField blockieren auf Nummern


    init {
        mainContent.children.addAll(minEmployeeBar,vacationsBlockerBar,maxVacationsBar,functionBox)
        center = centerStack
        setFieldsEditable(false)
        installWeekPopupHandlers()
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

    private fun installWeekPopupHandlers() {

        vacationsBlockTfl.setOnMouseClicked { e ->
            if (e.clickCount == 2) {
                val popup = weekPickerPopUp(
                    "Ferien Blocker",
                    weeks = weeksForBlocker,
                    onTextChanged = { txt -> vacationsBlockTfl.text = txt },
                    onClose = { closePopup() }
                )
                showPopup(popup.build())
                e.consume()
            }
        }

        vacationsSchoolBlockTfl.setOnMouseClicked { e ->
            if (e.clickCount == 2) {
                val popup = weekPickerPopUp(
                    "Schulferien",
                    weeks = weeksForSchool,
                    onTextChanged = { txt -> vacationsSchoolBlockTfl.text = txt },
                    onClose = { closePopup() }
                )
                showPopup(popup.build())
                e.consume()
            }
        }
    }

    private fun setFieldsEditable(editable: Boolean) {
        minEmployeeTfl.isDisable = !editable
        minStudentsTfl.isDisable = !editable
        minManagerTfl.isDisable = !editable
        vacationsUntil25Tfl.isDisable = !editable
        vacationFrom25Tfl.isDisable = !editable
        vacationFrom45Tfl.isDisable = !editable
        vacationsBlockTfl.isDisable = !editable
        vacationsSchoolBlockTfl.isDisable = !editable
        isEditMode = editable
    }

    private fun lockToPopupOnly(tfl: TextField) {
        tfl.isEditable = false
        tfl.isFocusTraversable = false
        tfl.addEventFilter(KeyEvent.ANY) { it.consume() }
    }


}