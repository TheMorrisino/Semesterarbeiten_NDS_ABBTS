/**
 * Wochenbereich innerhalb eines Jahresplans (KW 1..52).
 */
data class WeekRange(val startWeek: UInt, val endWeek: UInt) {
    init {
        require(startWeek in 1u..52u && endWeek in 1u..52u) {
            "Weeks must be in range 1..52 (got $startWeek..$endWeek)."
        }
        require(endWeek >= startWeek) { "endWeek must be >= startWeek (got $startWeek..$endWeek)." }
    }

    fun covers(week: UInt): Boolean = week in startWeek..endWeek
}
