class UI () : UserInterface {
    override fun showMessage(msg: String, type: MessageType) {
        val prefix = when (type) {
            MessageType.INFO -> "[INFO] "
            MessageType.WARNING -> "[WARN] "
            MessageType.ERROR -> "[ERROR] "
        }
        println(prefix + msg)
    }

    override fun readLine(prompt: String): String? {
        if (prompt.isNotEmpty()) print(prompt)
        return kotlin.io.readLine()
    }

    override fun readChoice(prompt: String): Int {
        val line = readLine(prompt)?.trim()
        return line?.toIntOrNull() ?: -1
    }

    override fun printMainMenu(year: Int) {
        println()
        println("=== Ressourcix (Jahresplan $year) ===")
        println("1) Mitarbeiter anzeigen")
        println("2) Mitarbeiter hinzufügen")
        println("3) Mitarbeiter löschen")
        println("4) Ferien automatisch generieren")
        println("5) Kalender anzeigen")
        println("6) Woche bewerten (OK/NOK)")
        println("0) Beenden")
        print("Auswahl: ")
    }



    override fun printCalendar(year: Int, employees: List<Employee>) {
        // Wir delegieren an die bestehende CalendarOutput‑Klasse,
        // weil dort bereits das Layout definiert ist.
        val output: CalendarOutput = ConsoleCalendarOutput()
        output.printYearPlan(year, employees)
    }
}