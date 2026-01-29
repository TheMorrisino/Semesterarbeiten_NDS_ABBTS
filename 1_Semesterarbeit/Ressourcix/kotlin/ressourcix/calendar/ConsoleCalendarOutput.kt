package ressourcix.calendar

import ressourcix.app.App
import ressourcix.domain.Employee
import ressourcix.domain.VacationEntry
import ressourcix.domain.VacationStatus
import ressourcix.domain.WeekRange
import ressourcix.domain.code
import ressourcix.domain.label
import ressourcix.ui.ConsoleIO
import ressourcix.gui.GuiBorderPane
import ressourcix.main

/**
 * Druckt eine Zeile pro Mitarbeiter und Spalten KW 1..52.
 */
class ConsoleCalendarOutput : CalendarOutput {
    private val year: UInt = 2026u
    override fun printYearPlan(year: UInt, employees: List<Employee>) {
        val weeks = 52u

        val header = buildString {
            append(String.format("%-12s", "EMP"))
            for (w in 1u..weeks) append(String.format("%4d", w.toInt()))
        }


        println()
        println("===== CALENDAR $year | Weeks 1..$weeks =====")
        println(header)
        println("-".repeat(header.length))

        for (e in employees) {
            val row = buildString {
                append(String.format("%-12s", e.label().take(12)))
                for (w in 1u..weeks) {
                    val status = statusForWeek(year, e, w)
                    append(String.format("%4s", status?.code ?: "."))
                }
            }
            println(row)
        }

        println()
        println("Legend: REQ=requested, APP=approved, GEN=generated, REJ=rejected, CAN=cancelled, .=free")
    }

    private fun statusForWeek(year: UInt, employee: Employee, week: UInt): VacationStatus? {
        val entries = employee.getVacationEntries().filter { it.year == year && it.hasWeek(week) }
        if (entries.isEmpty()) return null

        return entries.first().getStatus(week)

    }

    fun addVacationManual(empId: UInt, startWeek: UInt, endWeek: UInt) {

        val entry = VacationEntry(
            id = App.vacationIds.generateId(),
            employeeId = empId,
            year = year,
            range = WeekRange(startWeek, endWeek),
            initialStatus = VacationStatus.REQUESTED
        )
        println(App.management.addVacationSafe(App.management.employees[empId.toInt()],entry))
    }
}
