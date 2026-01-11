package ressourcix.ui.menu

import ressourcix.calendar.CalendarOutput
import ressourcix.calendar.ConsoleCalendarOutput
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.domain.Role
import ressourcix.domain.VacationPlanner
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
class MainMenu(
    private val io: ConsoleIO,
    private val management: EmployeeManagement,
    private val employeeIds: IdProvider,
    private val vacationIds: IdProvider,
    private val calendarOutput: CalendarOutput = ConsoleCalendarOutput(),
    private val year: UInt = 2026u
) {

    fun loop() {
        while (true) {
            printMainMenu()
            try {
                when (io.readChoice()) {
                    1 -> listEmployees()
                    2 -> addEmployee()
                    3 -> deleteEmployee()
                    4 -> generateVacations()
                    5 -> calendarOutput.printYearPlan(year, management.listAll())
                    6 -> rateSingleWeek()
                    7 -> addVacationManual()
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
        io.println("1) Mitarbeiter anzeigen")
        io.println("2) Mitarbeiter hinzufügen")
        io.println("3) Mitarbeiter löschen")
        io.println("4) Ferien automatisch generieren")
        io.println("5) Kalender anzeigen")
        io.println("6) Woche bewerten (OK/NO)")
        io.println("7) Ferien manuell hinzufügen")
        io.println("0) Beenden")
        io.print("Auswahl: ")
    }

    private fun listEmployees() {
        io.println()
        io.println("--- Mitarbeiter ---")
        management.listAll().forEach {
            io.println("ID=${it.getId()} | ${it.getAbbreviation().ifBlank { "??" }} | ${it.getFullName()}")
        }
    }

    private fun addEmployee() {
        io.println()
        val first = io.readNonBlank("Vorname: ") ?: run {
            io.println("Vorname darf nicht leer sein."); return
        }
        val last = io.readNonBlank("Nachname: ") ?: run {
            io.println("Nachname darf nicht leer sein."); return
        }

        val employee = Employee(employeeIds.generateId()).apply {
            setFirstName(first)
            setLastName(last)
            setWorkloadPercent(100u)
            setRole(Role.STAFF)
        }

        management.add(employee)
        io.println("Mitarbeiter hinzugefügt: ID=${employee.getId()} (${employee.getAbbreviation()})")
    }

    private fun deleteEmployee() {
        io.println()
        val id = io.readUInt("Mitarbeiter-ID zum Löschen: ", min = 1u) ?: run {
            io.println("Abgebrochen."); return
        }
        val ok = management.removeById(id)
        io.println(if (ok) "Gelöscht." else "Nicht gefunden.")
    }

    private fun generateVacations() {
        io.println()
        val warnings = VacationPlanner.generateForAll(
            year = year,
            employees = management.listAll(),
            nextVacationId = { vacationIds.generateId() },
            addVacationSafe = { emp, entry -> management.addVacationSafe(emp, entry) }
        )
        io.println("Ferien generiert (2-Wochen-Blöcke, Status=GEN).")
        if (warnings.isNotEmpty()) {
            io.println("Hinweise (Überschneidungen wurden übersprungen):")
            warnings.forEach { io.println("- $it") }
        }
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

        io.println("Neuer Status: 1=OK (APPROVED), 2=NO (REJECTED)")
        when (io.readChoice("Auswahl: ")) {
            1 -> {
                entry.setStatus(week, VacationStatus.APPROVED)
                io.println("KW $week -> OK")
            }
            2 -> {
                entry.setStatus(week, VacationStatus.REJECTED)
                io.println("KW $week -> NO")
            }
            else -> io.println("Abgebrochen.")
        }
    }

    private fun addVacationManual() {
        io.println()
        val empId = io.readUInt("Mitarbeiter-ID: ", min = 1u) ?: run {
            io.println("Abgebrochen."); return
        }
        val employee = management.findById(empId) ?: run {
            io.println("Mitarbeiter nicht gefunden."); return
        }

        val startWeek = io.readUInt("Start-Woche (1..52): ", min = 1u, max = 52u) ?: run {
            io.println("Abgebrochen."); return
        }
        val endWeek = io.readUInt("End-Woche (1..52): ", min = 1u, max = 52u) ?: run {
            io.println("Abgebrochen."); return
        }

        if (endWeek < startWeek) {
            io.println("Ungültiger Wochenbereich: End-Woche < Start-Woche.")
            return
        }

        val status = readStatus() ?: run {
            io.println("Abgebrochen."); return
        }

        val entry = VacationEntry(
            id = vacationIds.generateId(),
            employeeId = employee.getId(),
            year = year,
            range = WeekRange(startWeek, endWeek),
            initialStatus = status
        )

        val err = management.addVacationSafe(employee, entry)
        if (err != null) {
            io.println("Fehler: $err")
            return
        }

        io.println("OK: Ferien hinzugefügt: ${employee.label()} | KW $startWeek-$endWeek | Status=${status.name}")
    }

    private fun readStatus(): VacationStatus? {
        io.println("Status wählen:")
        io.println("1) REQUESTED")
        io.println("2) APPROVED")
        io.println("3) GENERATED")
        io.println("4) REJECTED")
        io.println("5) CANCELLED")
        return when (io.readChoice("Auswahl: ")) {
            1 -> VacationStatus.REQUESTED
            2 -> VacationStatus.APPROVED
            3 -> VacationStatus.GENERATED
            4 -> VacationStatus.REJECTED
            5 -> VacationStatus.CANCELLED
            else -> null
        }
    }
}
