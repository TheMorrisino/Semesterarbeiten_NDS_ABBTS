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
     val employees: MutableList<Employee> = mutableListOf()




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
            io.println("ID=${it.getId()} | ${it.abbreviationSting().ifBlank { "??" }} | ${it.getFullName()}")
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
        io.println("Mitarbeiter hinzugefügt: ID=${employee.getId()} (${employee.abbreviationSting()})")
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


    fun VacationEntry.overlapsWith(other: VacationEntry): Boolean {
        // Nur überlappen wenn gleiches Jahr
        if (this.year != other.year) return false

        // Wochen-Überschneidung prüfen
        return this.range.overlaps(other.range)
    }

    fun checkVacationOverlaps(maxAllowed: Int = 1) {
        val allVacation = employees.flatMap { it.getVacationEntries() }
        val overlaps = mutableListOf<Pair<VacationEntry, VacationEntry>>()

        for (i in allVacation.indices) {
            for (j in i + 1 until allVacation.size) {
                val vacation1 = allVacation[i]
                val vacation2 = allVacation[j]

                if (vacation1.overlapsWith(vacation2)) {
                    overlaps.add(Pair(vacation1, vacation2))
                }
            }
        }


        println("=== Überschneidungs-Check ===")
        println("Gefundene Überschneidungen: ${overlaps.size}")
        println("Maximal erlaubt: $maxAllowed")

        if (overlaps.size > maxAllowed) {
            println("WARNUNG: ${overlaps.size - maxAllowed} zu viele Überschneidung(en)!")
            println()
            println("Überschneidungen:")
            overlaps.forEach { (v1, v2) ->
                val emp1 = findById(v1.employeeId)
                val emp2 = findById(v2.employeeId)
                println("${emp1?.getFullName()} (Wochen ${v1.range.startWeek}-${v1.range.endWeek}) <-> ${emp2?.getFullName()} (Wochen ${v2.range.startWeek}-${v2.range.endWeek})")
            }
        } else {
            println("OK: Anzahl Überschneidungen im erlaubten Bereich.")
            overlaps.forEach { (v1, v2) ->
                val emp1 = findById(v1.employeeId)
                val emp2 = findById(v2.employeeId)
                println("${emp1?.getFullName()} (Wochen ${v1.range.startWeek}-${v1.range.endWeek}) <-> ${emp2?.getFullName()} (Wochen ${v2.range.startWeek}-${v2.range.endWeek})")
            }
        }

    }

}
