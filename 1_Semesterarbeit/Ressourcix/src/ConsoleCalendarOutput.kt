import kotlin.math.max

/**
 * Ausgabe des Jahresplans als Text-Tabelle in der Konsole.
 */
class ConsoleCalendarOutput(
    private val weeksPerYear: Int = 52,
    private val cellWidth: Int = 6,
    private val minEmpColWidth: Int = 4
) : CalendarOutput {

    override fun printYearPlan(year: Int, employees: List<Employee>) {
        if (employees.isEmpty()) {
            println("Jahresplan $year (keine Mitarbeiter)")
            return
        }

        val empColWidth = max(minEmpColWidth, employees.maxOf { it.getAbbreviation().ifBlank { "??" }.length })

        val header = buildHeader(empColWidth)
        println("Jahresplan $year")
        println(header)
        println("-".repeat(header.length))

        employees.forEach { employee ->
            println(buildRow(employee, year, empColWidth))
        }
    }

    private fun buildHeader(empColWidth: Int): String = buildString {
        append(padRight("EMP", empColWidth)).append(" | ")
        for (w in 1..weeksPerYear) {
            append(padRight("W%02d".format(w), cellWidth))
            if (w < weeksPerYear) append(" ")
        }
    }

    private fun buildRow(employee: Employee, year: Int, empColWidth: Int): String = buildString {
        val abbr = employee.getAbbreviation().ifBlank { "??" }
        append(padRight(abbr, empColWidth)).append(" | ")

        for (w in 1..weeksPerYear) {
            val cell = CalendarCellResolver.cellText(employee, year, w.toUInt())
            append(padRight(trimToWidth(cell, cellWidth), cellWidth))
            if (w < weeksPerYear) append(" ")
        }
    }

    private fun padRight(text: String, width: Int): String =
        if (text.length >= width) text else text + " ".repeat(width - text.length)

    private fun trimToWidth(text: String, width: Int): String {
        if (text.length <= width) return text
        if (width <= 1) return text.substring(0, width)
        return text.substring(0, width - 1) + "…"
    }
}
