package ressourcix.gui.pages

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import ressourcix.domain.Employee
import ressourcix.domain.VacationStatus
import ressourcix.domain.code
import ressourcix.calendar.consoleCalendarOutput
import ressourcix.app.app
import ressourcix.gui.popUp.vacationPopUp


object calenderView : StackPane() {

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

    private val employeecalenderView = app.employees
    private val tableView = TableView<Employee>()

    private val idColumn = TableColumn<Employee, UInt>("ID").apply {
        setCellValueFactory { ReadOnlyObjectWrapper(it.value.getId()) }
        prefWidth = 60.0
    }

    private val abbrevColumn = TableColumn<Employee, String>("Abkürzung").apply {
        setCellValueFactory { SimpleStringProperty(it.value.abbreviationSting()) }
        prefWidth = 120.0
    }

    // Cache: employeeId -> codes[week]
    private var currentYear: UInt = 2026u
    private val weekCodeCache: MutableMap<UInt, Array<String>> = mutableMapOf()

    init {
        tableView.columns.setAll(idColumn, abbrevColumn)
        tableView.items.addAll(employeecalenderView)


        val scroll = ScrollPane(tableView).apply {
            isFitToHeight = true
            isFitToWidth = true
            pannableProperty().set(true)


            tableView.setRowFactory {
                val row = TableRow<Employee>()
                row.setOnMouseClicked { event ->
                    if (event.clickCount == 2 && !row.isEmpty) {
                        val employee = row.item
                        onEmployeeDoubleClick(employee)
                    }
                }
                row
            }

        }
        children.addAll(scroll,dim,popupHost)

        // Standardjahr anzeigen
        showYear(2026u)
    }

    /** Baut Cache für alle Mitarbeiter für ein Jahr: KW1.KW52 */
    private fun rebuildCache(year: UInt, weeks: UInt = 52u) {
        weekCodeCache.clear()

        for (employee in employeecalenderView) {
            val codes = Array(weeks.toInt() + 1) { "." } // index 0 unbenutzt, Wochen starten bei 1

            val entries = employee.getVacationEntries().filter { it.year == year }

            val seen = BooleanArray(weeks.toInt() + 1)

            for (entry in entries) {
                val start = entry.range.startWeek.coerceAtLeast(1u)
                val end = entry.range.endWeek.coerceAtMost(weeks)

                for (week in start..end) {
                    val wi = week.toInt()
                    if (seen[wi]) {

                        throw IllegalStateException("Overlap detected: empId=${employee.getId()} year=$year week=$week")
                    }
                    seen[wi] = true

                    val status: VacationStatus? = entry.getStatus(week)
                    codes[wi] = status?.code ?: "."
                }
            }

            weekCodeCache[employee.getId()] = codes
        }
    }

    fun showYear(year: UInt, weeks: UInt = 52u) {
        currentYear = year

        tableView.items.setAll(employeecalenderView)

        rebuildCache(year, weeks)

        while (tableView.columns.size > 2) {
            tableView.columns.removeAt(tableView.columns.lastIndex)
        }

        for (week in 1u..weeks) {
            val title = "KW" + week.toString().padStart(2, '0')

            val weekCol = TableColumn<Employee, String>(title).apply {
                prefWidth = 45.0
                setCellValueFactory { cell ->
                    val empId = cell.value.getId()
                    val code = weekCodeCache[empId]?.get(week.toInt()) ?: "."
                    SimpleStringProperty(code)
                }
            }

            tableView.columns.add(weekCol)
        }

        tableView.refresh()
    }

    /** Aufrufen, wenn Ferien/Status **/
    fun refreshVacations() {
        rebuildCache(currentYear, 52u)
        tableView.refresh()
    }

    /** Falls Mitarbeiterliste geändert wurde */
    fun updateEmployees() {
        tableView.items.setAll(employeecalenderView)
        showYear(currentYear) // baut Spalten + Cache neu
    }


    private fun onEmployeeDoubleClick(employee: Employee) {
        val empId = employee.getId()
        println("Doppelklick auf: $empId ${employee.abbreviationSting()}")

        // optional: Felder im Popup vorbefüllen
        vacationPopUp.idField.text = empId.toString()
        vacationPopUp.abbreviationField.text = employee.abbreviationSting()

        showPopup(
            vacationPopUp.build(
                onClose = { closePopup() },
                onSave = { kw ->
                    consoleCalendarOutput.addVacation(empId, kw.startKW, kw.endKW)
                    println("Ferien: ${kw.startKW} - ${kw.endKW}")
                    refreshVacations()
                    closePopup()
                }
            )
        )
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
    dim.isVisible = false
    popupHost.isVisible = false
}

    fun showSimplePopup(
        title: String = "Ferieneintrag hinzufügen",
        header: String? = null,
        message: String,
        type: Alert.AlertType = Alert.AlertType.INFORMATION
    ) {
        Alert(type).apply {
            this.title = title
            this.headerText = header
            this.contentText = message

        }.showAndWait()
    }

}