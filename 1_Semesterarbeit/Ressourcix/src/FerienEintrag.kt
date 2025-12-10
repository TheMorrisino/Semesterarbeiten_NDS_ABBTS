import java.lang.Thread.sleep

class FerienEintrag (
    var id: UInt,


) {

    // To Do mitarbeiterId parameter vorbereiten
    var jahr: Int = 100
    var art = FerienArt.OFFEN
    var status = FerienStatus.OFFEN
    var prioritaet: Int = 0

    //    var mitarbeiterId =
    var kommentar: String = "empty"
    var holidayWeeks : MutableList<HolidayWeek> = mutableListOf()
    var holidayWeek = HolidayWeek()


    fun add(holidayWeek : HolidayWeek) {
        val holiday = HolidayWeek(holidayWeek.startWeek, holidayWeek.endWeek)
        holidayWeeks.add(holiday)

    }
    fun remove (week: HolidayWeek) {
        val removed = holidayWeeks.remove(week)   // true, wenn etwas entfernt wurde
        if (removed) {
            println("Entfernt: $week")
        } else {
            println("Kein passender Eintrag gefunden für: $week")
        }
    }


    override fun toString(): String {

        fun weeksAsString(): String =
            if (holidayWeeks.isEmpty()) {
                "keine"
            } else {
                // Jede Woche als "[start‑end]" darstellen und mit Komma trennen
                holidayWeeks.joinToString(separator = ", ") {
                    "[${it.startWeek}-${it.endWeek}]"
                }
            }


        return buildString {
            appendLine("FerienEintrag {")
            appendLine("  id          = $id")
            appendLine("  jahr        = $jahr")
            appendLine("  art         = $art")
            appendLine("  status      = $status")
            appendLine("  prioritaet  = $prioritaet")
            appendLine("  kommentar   = \"$kommentar\"")
            appendLine("  holidayWeeks= ${weeksAsString()}")
            append("}")
        }
    }


}