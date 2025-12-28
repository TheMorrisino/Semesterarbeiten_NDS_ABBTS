/**
 * Ferien-Eintrag eines Mitarbeiters für ein Jahr.
 *
 * Wichtig: Der Status wird pro Woche gespeichert, damit einzelne Wochen
 * genehmigt/abgelehnt werden können.
 */
class VacationEntry(
    val id: UInt,
    val employeeId: UInt,
    val year: Int
) {
    private val weekStatus: MutableMap<UInt, VacationStatus> = mutableMapOf()

    fun weeks(): Set<UInt> = weekStatus.keys

    fun hasWeek(week: UInt): Boolean = weekStatus.containsKey(week)

    fun getStatus(week: UInt): VacationStatus? = weekStatus[week]

    fun setStatus(week: UInt, status: VacationStatus) {
        require(week in 1u..52u) { "week must be in 1..52." }
        require(hasWeek(week)) { "week $week is not part of this VacationEntry." }
        weekStatus[week] = status
    }

    fun addWeek(week: UInt, status: VacationStatus = VacationStatus.GENERATED) {
        require(week in 1u..52u) { "week must be in 1..52." }
        weekStatus[week] = status
    }

    /**
     * Fügt einen Wochenbereich hinzu.
     * Wenn possibleBlockSize=2, werden 2-Wochen-Blöcke bevorzugt.
     */
    fun addRange(range: WeekRange, status: VacationStatus = VacationStatus.GENERATED) {
        for (w in range.startWeek..range.endWeek) {
            addWeek(w, status)
        }
    }
}
