package ressourcix.domain

/**
 * ISO-Kalenderwochen-Bereich (1..52 oder 53).
 */
data class WeekRange(val startWeek: UInt, val endWeek: UInt) {
    init {
        require(startWeek in 1u..53u) { "startWeek must be 1..53" }
        require(endWeek in 1u..53u) { "endWeek must be 1..53" }
        require(startWeek <= endWeek) { "startWeek must be <= endWeek" }
    }

    fun contains(week: UInt): Boolean = week in startWeek..endWeek

    fun overlaps(other: WeekRange): Boolean =
        !(endWeek < other.startWeek || startWeek > other.endWeek)
}
