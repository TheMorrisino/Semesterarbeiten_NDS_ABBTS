/**
 * Entscheidet, was in einer Kalenderzelle (Mitarbeiter x Woche) angezeigt wird.
 *
 */
object CalendarCellResolver {

    fun cellText(employee: Employee, year: Int, week: UInt): String {
        val entry = employee.getVacationEntries()
            .firstOrNull { it.year == year && it.hasWeek(week) }
            ?: return ""

        return when (entry.getStatus(week)) {
            VacationStatus.GENERATED -> "GEN"
            VacationStatus.APPROVED -> "OK"
            VacationStatus.REJECTED -> "NO"
            null -> ""
        }
    }
}
