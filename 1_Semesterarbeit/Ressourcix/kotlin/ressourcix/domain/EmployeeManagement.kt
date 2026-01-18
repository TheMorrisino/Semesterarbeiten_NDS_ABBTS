package ressourcix.domain

import ressourcix.ui.ConsoleIO
import ressourcix.util.IdProvider

/**
 * Kleine "Verwaltung" (in-memory).
 * - kennt Mitarbeiter
 * - findet nach ID
 * - überprüft Überschneidungen bei Ferien
 */
class EmployeeManagement () {
    private val employees: MutableList<Employee> = mutableListOf()
     fun mitarbeiterVerwaltung(io: ConsoleIO , management: EmployeeManagement , employeeIds: IdProvider) {
        io.println()
        io.println("=== Ressourcix Mitarbeiter Verwaltung ===")
        io.println("1) Mitarbeiter anzeigen")
        io.println("2) Mitarbeiter hinzufügen")
        io.println("3) Mitarbeiter löschen")
        io.println("0) Beenden")
        io.print("Auswahl: ")

         while(true) {
             when (io.readChoice()) {
                 1 -> listEmployees(io,management)
                 2 -> addEmployee(io,management,employeeIds)
                 3 -> deleteEmployee(io,management)
                 0 -> return
                 else -> io.println("Unbekannte Auswahl.")
             }
         }
    }

    private fun listEmployees(io: ConsoleIO,management: EmployeeManagement) {
        println()
        println("--- Mitarbeiter ---")
        management.listAll().forEach {
            io.println("ID=${it.getId()} | ${it.getAbbreviation().ifBlank { "??" }} | ${it.getFullName()}")
        }
    }
    private fun addEmployee(io: ConsoleIO , management: EmployeeManagement , employeeIds: IdProvider) {
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

    private fun deleteEmployee(io: ConsoleIO, management: EmployeeManagement) {
        io.println()
        val id = io.readUInt("Mitarbeiter-ID zum Löschen: ", min = 1u) ?: run {
            io.println("Abgebrochen."); return
        }
        val ok = management.removeById(id)
        io.println(if (ok) "Gelöscht." else "Nicht gefunden.")
    }



    fun listAll(): List<Employee> = employees.toList()

    fun add(employee: Employee) {
        employees.add(employee)
    }

    fun removeById(id: UInt): Boolean {
        val idx = employees.indexOfFirst { it.getId() == id }
        if (idx == -1) return false
        employees.removeAt(idx)
        return true
    }

    fun findById(id: UInt): Employee? = employees.firstOrNull { it.getId() == id }

    /**
     * Fügt Ferien nur hinzu, wenn es keine Überschneidung im gleichen Jahr gibt.
     * Rückgabe:
     * - null = OK
     * - String = Fehlertext
     */
    fun addVacationSafe(employee: Employee, entry: VacationEntry): String? {
        val overlaps = employee.getVacationEntries()
            .filter { it.year == entry.year }
            .any { it.range.overlaps(entry.range) }

        if (overlaps) {
            return "Überschneidung: ${employee.label()} hat bereits Ferien im überlappenden Bereich."
        }

        employee.addVacationEntry(entry)
        return null
    }

    /**
     * Demo-Seed: 10 Mitarbeiter IDs 1..10.
     */
    fun seed10Employees() {
        employees.clear()
        val ids = IdProvider(start = 1u)
        val names = listOf(
            "Max" to "Müller",
            "Sara" to "Schmidt",
            "Lena" to "Weber",
            "Noah" to "Meier",
            "Mia" to "Keller",
            "Leo" to "Fischer",
            "Emma" to "Brunner",
            "Paul" to "Baumann",
            "Nina" to "Steiner",
            "Tom" to "Hug"
        )
        for ((first, last) in names) {
            val e = Employee(ids.generateId()).apply {
                setFirstName(first)
                setLastName(last)
                setRole(Role.STAFF)
                setWorkloadPercent(100u)
            }
            employees.add(e)
        }
    }

    fun readAllEmVacation () {
        var allVacation = employees

    }

    fun showAllEmVacation () {

    }

}
