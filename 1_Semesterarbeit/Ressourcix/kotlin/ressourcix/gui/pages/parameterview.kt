//  Autor:        Pedro Santos

package ressourcix.gui.pages

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.scene.input.KeyEvent
import ressourcix.domain.config
import ressourcix.gui.popups.weekPickerPopUp
import ressourcix.logger.logger

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

//TODO Clean Code bearbeitung mit employeemanagementView.kt

object parameterview: BorderPane() {

    private val weeksForBlocker = BooleanArray(53)
    private val weeksForSchool  = BooleanArray(53)

    private var isEditMode: Boolean = false

    val minEmployeeTfl  = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,50)
    }
    val minApprenticeTfl  = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,50)
    }
    val minManagerTfl  = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,50)
    }

    val vacationsBlockTfl = createTfl("","Blockierte Wochen eintragen...").apply {
        lockToPopupOnly(this)
    }
    val vacationsSchoolBlockTfl = createTfl("","Schulferien eintragen").apply {
        lockToPopupOnly(this)
    }

    val vacationsUntil25Tfl = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,52)
    }
    val vacationFrom25Tfl = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,52)
    }
    val vacationFrom45Tfl = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(2,52)
    }

    private val parameterChangeBtn = createButton("Parameter \nändern").apply {
        setOnAction {
            setFieldsEditable(true)
            logger.info("Parameter wurden entsperrt.")}
    }
    private val parameterSaveBtn = createButton("Parameter \nspeichern").apply {
        setOnAction{
            if (!isEditMode) return@setOnAction
            saveFromUiToConfig()
            setFieldsEditable(false)
            calenderView.refreshVacations()
            logger.info("Parameter wurden erfolgreich gespeichert.")
        }
    }
    private val parameterRestoreBtn = createButton("Parameter \nzurücksetzen").apply {
        setOnAction {
            loadFromConfigToUi()
            setFieldsEditable(false)
            logger.info("Parameter wurden erfolgreich zurückgesetzt.")}
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
        val minStudents = createDataBox("Min. Anzahl Lehrlinge",minApprenticeTfl)
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
    }

    init {
        mainContent.children.addAll(minEmployeeBar,vacationsBlockerBar,maxVacationsBar,functionBox)
        center = centerStack
        setFieldsEditable(false)
        installWeekPopupHandlers()
        loadFromConfigToUi()
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

    private fun positiveIntNoZeroFormatter(maxDigits: Int,maxValue: Int): TextFormatter<String> {
        return TextFormatter { change ->
            val newText = change.controlNewText
            val ok = newText.isEmpty() || (newText.matches(Regex("[1-9][0-9]*"))
                    && newText.length <= maxDigits
                    && newText.toInt() <= maxValue)
            if (ok) change else null
        }
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
        minApprenticeTfl.isDisable = !editable
        minManagerTfl.isDisable = !editable
        vacationsUntil25Tfl.isDisable = !editable
        vacationFrom25Tfl.isDisable = !editable
        vacationFrom45Tfl.isDisable = !editable
        vacationsBlockTfl.isDisable = !editable
        vacationsSchoolBlockTfl.isDisable = !editable
        parameterSaveBtn.isDisable = !editable
        isEditMode = editable
    }

    private fun lockToPopupOnly(tfl: TextField) {
        tfl.isEditable = false
        tfl.isFocusTraversable = false
        tfl.addEventFilter(KeyEvent.ANY) { it.consume() }
    }

    private fun copyWeeks(src: BooleanArray, dst: BooleanArray) {
        for (i in dst.indices) dst[i] = false
        val n = minOf(src.size, dst.size)
        for (i in 0 until n) dst[i] = src[i]
    }

    private fun weeksToText(weeks: BooleanArray): String {
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


    private fun loadFromConfigToUi() {
        minEmployeeTfl.text = config.minEmployeeNumber.toString()
        minApprenticeTfl.text = config.minApprenticeNumber.toString()
        minManagerTfl.text = config.minManagerNumber.toString()
        vacationsUntil25Tfl.text = config.vacation25Years.toString()
        vacationFrom25Tfl.text = config.vacationOver25Years.toString()
        vacationFrom45Tfl.text = config.vacationOver50Years.toString()
        copyWeeks(src = config.vacationBlock, dst = weeksForBlocker)
        copyWeeks(src = config.vacationSchoolBlock, dst = weeksForSchool)
        vacationsBlockTfl.text = weeksToText(weeksForBlocker)
        vacationsSchoolBlockTfl.text = weeksToText(weeksForSchool)
    }

    private fun saveFromUiToConfig() {
        config.minEmployeeNumber = minEmployeeTfl.text.trim().toIntOrNull() ?: 0
        config.minApprenticeNumber = minApprenticeTfl.text.trim().toIntOrNull() ?: 0
        config.minManagerNumber = minManagerTfl.text.trim().toIntOrNull() ?: 0
        config.vacation25Years = vacationsUntil25Tfl.text.trim().toIntOrNull() ?: config.vacation25Years
        config.vacationOver25Years = vacationFrom25Tfl.text.trim().toIntOrNull() ?: config.vacationOver25Years
        config.vacationOver50Years = vacationFrom45Tfl.text.trim().toIntOrNull() ?: config.vacationOver50Years
        config.vacationBlock = weeksForBlocker.copyOf()
        config.vacationSchoolBlock = weeksForSchool.copyOf()
    }



}