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


    fun addHolidayWeek(holidayWeek : HolidayWeek) {
        val holiday = HolidayWeek(holidayWeek.startWeek, holidayWeek.endWeek)
        if (holiday.startWeek in 1u..52u && holiday.endWeek in 1u..52u) {
            holidayWeeks.add(holiday)
        } else {
            println("Ungültige KW Woche: ${holiday.startWeek}–${holiday.endWeek}. " +
                    "Erlaubt ist nur KW 1 bis KW 52.")
        }

    }
    fun removeHolidayWeek (week: HolidayWeek) {
        val removed = holidayWeeks.remove(week)   // true, wenn etwas entfernt wurde
        if (removed) {
            println("Entfernt: $week")
        } else {
            println("Kein passender Eintrag gefunden für: $week")
        }
    }

    fun changeHolidayWeek (oldHolidayWeek: HolidayWeek, newHolidayWeek: HolidayWeek)   {

       if (oldHolidayWeek in holidayWeeks) {
            // Suche im Index
           val index = holidayWeeks.indexOfFirst {
           ((it.startWeek == oldHolidayWeek.startWeek) and (it.endWeek == oldHolidayWeek.endWeek)) }
           // Wenn gefunden ersetzte diesen Index mit neuen Werten
           holidayWeeks[index] = newHolidayWeek
           println("Alter Eintrag $oldHolidayWeek hat Index: $index")
           // Überprüfung, ob neuer Index der gleiche ist wie der alte
           val indexNew = holidayWeeks.indexOfFirst {
               ((it.startWeek == newHolidayWeek.startWeek) and (it.endWeek == newHolidayWeek.endWeek)) }
           println("Alter Eintrag wird mit neuem ersetzt $newHolidayWeek hat Index: $indexNew")


        } else {
           println("Eintrag nicht gefunden")
           println("Debugging")
           for ((index, oldHolidayWeek) in holidayWeeks.withIndex()) {
               println("Index $index, Wert $oldHolidayWeek")
        }





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