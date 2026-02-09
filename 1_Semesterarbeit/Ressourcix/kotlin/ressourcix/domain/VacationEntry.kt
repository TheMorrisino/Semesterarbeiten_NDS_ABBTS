package ressourcix.domain

/**
 * Ferien-Eintrag für einen Mitarbeiter.
 *
 * Besonderheit:
 * - pro Woche wird ein Status gespeichert -> damit kann man einzelne Wochen bewerten.
 */
class VacationEntry(
    val id: UInt,
    val employeeId: UInt,
    val year: UInt,
    val range: WeekRange,
    initialStatus: VacationStatus = VacationStatus.GENERATED
) {
    private val statusByWeek: MutableMap<UInt, VacationStatus> = mutableMapOf()

    init {
        for (w in range.startWeek..range.endWeek) {
            statusByWeek[w] = initialStatus
        }
    }

    fun hasWeek(week: UInt): Boolean = range.contains(week)

    fun getStatus(week: UInt): VacationStatus? = statusByWeek[week]


    fun setStatus(week: UInt, status: VacationStatus) {
        require(hasWeek(week)) { "week $week is not part of this VacationEntry (range ${range.startWeek}-${range.endWeek})." }
        statusByWeek[week] = status
    }

    override fun toString(): String {
        return "Urlaub #$id (MA-ID: $employeeId) - Jahr $year, Wochen ${range.startWeek}-${range.endWeek}"
    }
}
