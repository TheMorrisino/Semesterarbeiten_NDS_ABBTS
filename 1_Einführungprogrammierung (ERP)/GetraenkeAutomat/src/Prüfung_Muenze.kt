enum class Prüfung_Muenze (val wert: Double) {
    RP10 (0.10),
    RP20 (0.20),
    RP50 (0.50),
    FR1  (1.0),
    FR2  (2.0),
    FR5 (5.0);

    companion object {
        fun Muenzen_Validieren (wert: Double) : Boolean {
            return values().any { it.wert == wert }
        }
    }
}