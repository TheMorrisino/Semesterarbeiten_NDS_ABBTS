package ressourcix.domain

import ressourcix.util.IdProvider

/**
 * Kleine "Verwaltung" (in-memory).
 * - kennt Mitarbeiter
 * - findet nach ID
 * - überprüft Überschneidungen bei Ferien
 */
class EmployeeManagement {
    private val employees: MutableList<Employee> = mutableListOf()

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
}
