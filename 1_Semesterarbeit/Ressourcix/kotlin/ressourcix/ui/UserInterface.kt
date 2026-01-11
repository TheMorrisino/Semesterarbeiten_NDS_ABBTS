package ressourcix.ui

import ressourcix.domain.Employee

// in Progress
interface UserInterface {
    /** Zeigt eine Nachricht an (Info, Warnung, Fehler). */
    fun showMessage(msg: String, type: MessageType = MessageType.INFO)

    /** Liest eine ganze Zeile vom Nutzer ein. */
    fun readLine(prompt: String = ""): String?

    /** Liest eine Ganzzahl‑Auswahl ein (z.B. Menü). */
    fun readChoice(prompt: String = ""): Int

    /** Gibt das Hauptmenü aus. */
    fun printMainMenu(year: Int)

    /** Gibt die aktuelle Jahresplanung aus (Kalender). */
    fun printCalendar(year: Int, employees: List<Employee>)

}