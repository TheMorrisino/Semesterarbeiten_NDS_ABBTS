package ressourcix.gui.pages

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import ressourcix.domain.Employee
import ressourcix.domain.VacationStatus
import ressourcix.domain.code
import ressourcix.gui.GuiBorderPane
import ressourcix.gui.popUp.FerienantragKwPopup
import ressourcix.calendar.ConsoleCalendarOutput

object calenderView : StackPane() {

    private val employees = GuiBorderPane.graphical.employees
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
        tableView.items.addAll(employees)


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
        children.add(scroll)

        // Standardjahr anzeigen
        showYear(2026u)
    }

    /** Baut Cache für alle Mitarbeiter für ein Jahr: KW1.KW52 */
    private fun rebuildCache(year: UInt, weeks: UInt = 52u) {
        weekCodeCache.clear()

        for (employee in GuiBorderPane.graphical.employees) {
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

        tableView.items.setAll(GuiBorderPane.graphical.employees)

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
        tableView.items.setAll(GuiBorderPane.graphical.employees)
        showYear(currentYear) // baut Spalten + Cache neu
    }


    private fun onEmployeeDoubleClick(employee: Employee) {
        println("Doppelklick auf: ${employee.getId()} ${employee.abbreviationSting()}")
        FerienantragKwPopup.show()?.let {
            ConsoleCalendarOutput().addVacationManual(employee.getId(), it.startKW, it.endKW)
            println("Ferien: ${it.startKW} - ${it.endKW}")
        }

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
