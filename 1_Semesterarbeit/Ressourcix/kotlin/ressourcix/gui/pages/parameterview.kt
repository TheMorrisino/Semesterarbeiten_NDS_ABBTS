// Autor: Pedro Santos

package ressourcix.gui.pages
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.input.KeyEvent
import ressourcix.domain.config
import gui.popUp.weekPickerPopUp
import ressourcix.logger.logger
import ressourcix.gui.util.createButton
import ressourcix.gui.util.createDataBox
import ressourcix.gui.util.createTfl
import ressourcix.gui.util.positiveIntNoZeroFormatter
import ressourcix.gui.components.PopUpLayer

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

    private val popupLayer = PopUpLayer()

    private val mainContent = VBox().apply {
        padding = Insets(50.0)
        alignment = Pos.CENTER
        spacing = 50.0
    }
    private val vacationsBlockerBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val vacationsBlockDataB = createDataBox("Ferien Blocker",vacationsBlockTfl)
        val vacationsSchoolBlockDataB = createDataBox("Schulferien", vacationsSchoolBlockTfl)

        children.addAll(vacationsBlockDataB,vacationsSchoolBlockDataB)

    }
    private val functionBox = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.BOTTOM_CENTER
        spacing = 80.0

        children.addAll(parameterChangeBtn,parameterSaveBtn,parameterRestoreBtn)
    }
/*
Zukunft Feature

    private val maxVacationsBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.TOP_CENTER

        val maxVacationUntil25DataB = createDataBox("Anzahl Ferien bis 25 Jahre alt",vacationsUntil25Tfl)
        val maxVacationFrom25DataB = createDataBox("Anzahl Ferien ab 25 Jahre alt",vacationFrom25Tfl)
        val maxVacationFrom45DataB = createDataBox("Anzahl Ferien ab 45 Jahre alt", vacationFrom45Tfl)

        children.addAll(maxVacationUntil25DataB,maxVacationFrom25DataB,maxVacationFrom45DataB)

    }
    private val minEmployeeBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.BOTTOM_CENTER

        val minEmployee = createDataBox("Min. Anzahl Mitarbeiter",minEmployeeTfl)
        val minStudents = createDataBox("Min. Anzahl Lehrlinge",minApprenticeTfl)
        val minManage = createDataBox("Min. Anzahl Leiter", minManagerTfl)

        children.addAll(minEmployee,minStudents,minManage)
    }
 */

    init {
/*
Zukunft Feature

mainContent.children.addAll(minEmployeeBar,vacationsBlockerBar,maxVacationsBar,functionBox)
*/
        mainContent.children.addAll(vacationsBlockerBar,functionBox)
        center = popupLayer.wrap(mainContent) { popupLayer.close() }
        setFieldsEditable(false)
        installWeekPopupHandlers()
        loadFromConfigToUi()
    }

    private fun installWeekPopupHandlers() {

        vacationsBlockTfl.setOnMouseClicked { e ->
            if (e.clickCount == 2) {
                val popup = weekPickerPopUp(
                    "Ferien Blocker",
                    weeks = weeksForBlocker,
                    onTextChanged = { txt -> vacationsBlockTfl.text = txt },
                    onClose = { popupLayer.close() }
                )
                popupLayer.show(popup.build())
                e.consume()
            }
        }

        vacationsSchoolBlockTfl.setOnMouseClicked { e ->
            if (e.clickCount == 2) {
                val popup = weekPickerPopUp(
                    "Schulferien",
                    weeks = weeksForSchool,
                    onTextChanged = { txt -> vacationsSchoolBlockTfl.text = txt },
                    onClose = { popupLayer.close() }
                )
                popupLayer.show(popup.build())
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