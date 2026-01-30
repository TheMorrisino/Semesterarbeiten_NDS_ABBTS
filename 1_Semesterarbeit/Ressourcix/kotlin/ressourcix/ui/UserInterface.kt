package ressourcix.ui

import ressourcix.calendar.CalendarOutput
import ressourcix.calendar.consoleCalendarOutput
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.util.IdProvider
import java.awt.TrayIcon

// in Progress
interface UserInterface {

    /** Zeigt eine Nachricht an (Info, Warnung, Fehler). */
    fun showMessage(msg: String, type: TrayIcon.MessageType = TrayIcon.MessageType.INFO) //type: Message = MessageType.INFO)

    /** Liest eine ganze Zeile vom Nutzer ein. */
    fun readLine(prompt: String = ""): String?

    /** Liest eine Ganzzahl‑Auswahl ein (z.B. Menü). */
    fun readChoice(prompt: String = ""): Int

    /** Gibt das Hauptmenü aus. */
    fun printMainMenu(year: Int)
//
//    /** Gibt die aktuelle Jahresplanung aus (Kalender). */
//    fun printCalendar(year: Int, employees: List<Employee>)

}