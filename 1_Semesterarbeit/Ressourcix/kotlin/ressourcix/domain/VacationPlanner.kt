package ressourcix.domain

/**
 * Automatischer Ferien-Generator (Demo):
 * Generiert feste 2-Wochen-Blöcke.
 */
object VacationPlanner {

    fun generateForAll(
        year: UInt,
        employees: List<Employee>,
        nextVacationId: () -> UInt,
        addVacationSafe: (Employee, VacationEntry) -> String?,
        blocks: List<WeekRange> = listOf(
            WeekRange(10u, 11u),
            WeekRange(20u, 21u),
            WeekRange(30u, 31u)
        )
    ): List<String> {
        val warnings = mutableListOf<String>()
        for (emp in employees) {
            for (range in blocks) {
                val entry = VacationEntry(
                    id = nextVacationId(),
                    employeeId = emp.getId(),
                    year = year,
                    range = range,
                    initialStatus = VacationStatus.GENERATED
                )
                val err = addVacationSafe(emp, entry)
                if (err != null) warnings.add(err)
            }
        }
        return warnings
    }
}
