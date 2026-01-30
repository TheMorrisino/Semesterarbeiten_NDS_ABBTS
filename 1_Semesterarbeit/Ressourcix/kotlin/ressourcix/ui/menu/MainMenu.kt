package ressourcix.ui.menu

import ressourcix.calendar.CalendarOutput
import ressourcix.calendar.consoleCalendarOutput
import ressourcix.domain.EmployeeManagement
import ressourcix.domain.VacationStatus
import ressourcix.domain.WeekRange
import ressourcix.domain.VacationEntry
import ressourcix.domain.label
import ressourcix.ui.ConsoleIO
import ressourcix.util.IdProvider


/**
 * Hauptmenü der Konsolen-App.
 * Enthält Fehlerbehandlung (try/catch), damit die App nicht abstürzt.
 */
object mainMenu {

    lateinit var io: ConsoleIO
    lateinit var management: EmployeeManagement
    lateinit var employeeIds: IdProvider
    lateinit var vacationIds: IdProvider

    fun init(
        io: ConsoleIO,
        management: EmployeeManagement,
        employeeIds: IdProvider,
        vacationIds: IdProvider
    ) {
        this.io = io
        this.management = management
        this.employeeIds = employeeIds
        this.vacationIds = vacationIds
    }

    val year: UInt = 2026u
    fun loop() {
        while (true) {
            printMainMenu()
            try {
                when (io.readChoice()) {
                    1 -> { management.mitarbeiterVerwaltung(io,management,employeeIds)}
                    2 -> consoleCalendarOutput.printYearPlan(year, management.listAll())
                    3 -> rateSingleWeek()
                    0 -> return
                    else -> io.println("Unbekannte Auswahl.")
                }
            } catch (e: Exception) {
                io.println("FEHLER: ${e.message}")
            }
        }
    }

    private fun printMainMenu() {
        io.println()
        io.println("=== Ressourcix (Jahresplan $year) ===")
        io.println("1) Mitarbeiter Verwaltung")
        io.println("2) Kalender anzeigen")
        io.println("3) Woche bewerten (OK/NOK)")
        io.println("4) Ferien manuell hinzufügen")
        io.println("0) Beenden")
        io.print("Auswahl: ")
    }

    private fun rateSingleWeek() {
        io.println()
        val empId = io.readUInt("Mitarbeiter-ID: ", min = 1u) ?: run {
            io.println("Abgebrochen."); return
        }
        val employee = management.findById(empId) ?: run {
            io.println("Mitarbeiter nicht gefunden."); return
        }

        val week = io.readUInt("Woche (1..52): ", min = 1u, max = 52u) ?: run {
            io.println("Abgebrochen."); return
        }

        val entry = employee.getVacationEntries()
            .firstOrNull { it.year == year && it.hasWeek(week) }
            ?: run {
                io.println("Kein Ferien-Eintrag für diese Woche (erst generieren oder manuell hinzufügen).")
                return
            }

        io.println("Neuer Status: 1=OK (APPROVED), 2=NOK (REJECTED)")
        when (io.readChoice("Auswahl: ")) {
            1 -> {
                entry.setStatus(week, VacationStatus.APPROVED)
                io.println("KW $week -> OK")
            }
            2 -> {
                entry.setStatus(week, VacationStatus.REJECTED)
                io.println("KW $week -> NOK")
            }
            else -> io.println("Abgebrochen.")
        }
    }




}
