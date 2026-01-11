/**
 * Sehr einfacher Generator, der für alle Mitarbeiter Ferien "generiert".
 *
 * Anforderungen:
 * - Alle generierten Wochen werden als GENERATED markiert.
 * - Ferien sollen wenn möglich in 2-Wochen-Blöcken gesetzt werden.
 */
object VacationPlanner {

    /**
     * Setzt/überschreibt die Ferien für alle Mitarbeiter.
     *
     * Strategie (minimal):
     * - Jeder Mitarbeiter bekommt 2 Blöcke à 2 Wochen.
     * - Block 1 startet bei KW 1 + index*2
     * - Block 2 startet bei KW 30 + index*2
     */
    fun generateForAll(year: Int, employees: List<Employee>, nextVacationId: () -> UInt) {
        employees.forEachIndexed { index, employee ->
            // Überschreibt bestehende Ferien, damit "Generieren" reproduzierbar ist.
            employee.clearVacationEntries()

            // Für Minimalität: wir hängen einfach einen neuen VacationEntry an.
            val entry = VacationEntry(
                id = nextVacationId(),
                employeeId = employee.getId(),
                year = year
            )

            val start1 = (1 + index * 2).toUInt()
            val start2 = (30 + index * 2).toUInt()

            addTwoWeekBlock(entry, start1)
            addTwoWeekBlock(entry, start2)

            employee.addVacationEntry(entry)
        }
    }

    private fun addTwoWeekBlock(entry: VacationEntry, startWeek: UInt) {
        // Wenn wir über 52 kommen, machen wir minimal nichts mehr.
        if (startWeek > 52u) return
        val endWeek = if (startWeek + 1u <= 52u) startWeek + 1u else startWeek
        entry.addRange(WeekRange(startWeek, endWeek), VacationStatus.GENERATED)
    }
}
