/**
 * Minimal lauffähige Konsolen-App.
 *
 * Features:
 * - 10 Start-Mitarbeiter ("Datenbank")
 * - Menü: Mitarbeiter anlegen/löschen/listen
 * - Ferien automatisch generieren (2-Wochen-Blöcke) -> Status GENERATED
 * - Kalender in der Konsole anzeigen
 * - Einzelne Woche bewerten (APPROVED/REJECTED)
 */

var ui : UserInterface = UI()

fun main() {
    val year = 2026

    val management = EmployeeManagement() // enthält bereits 10 Seed-Mitarbeiter
    val employeeIds = IdProvider(start = 11u) // Seed nutzt 1..10
    val vacationIds = IdProvider(start = 1u)

    val calendarOutput: CalendarOutput = ConsoleCalendarOutput()



    while (true) {
        ui.printMainMenu(year)
        when (ui.readChoice()) {
            1 -> listEmployees(management)
            2 -> addEmployee(management, employeeIds)
            3 -> deleteEmployee(management)
            4 -> generateVacations(year, management, vacationIds)
            5 -> calendarOutput.printYearPlan(year, management.listAll())
            6 -> rateSingleWeek(year, management)
            0 -> return
            else -> ui.showMessage("Unbekannte Auswahl.", MessageType.WARNING)
        }
    }
}

//private fun printMainMenu(year: Int) {
//    println()
//    println("=== Ressourcix (Jahresplan $year) ===")
//    println("1) Mitarbeiter anzeigen")
//    println("2) Mitarbeiter hinzufügen")
//    println("3) Mitarbeiter löschen")
//    println("4) Ferien automatisch generieren")
//    println("5) Kalender anzeigen")
//    println("6) Woche bewerten (OK/NO)")
//    println("0) Beenden")
//    print("Auswahl: ")
//}

//private fun readChoice(): Int = readLine()?.trim()?.toIntOrNull() ?: -1

private fun listEmployees(management: EmployeeManagement) {
    println()
    println("--- Mitarbeiter ---")
    management.listAll().forEach {
        println("ID=${it.getId()} | ${it.getAbbreviation().ifBlank { "??" }} | ${it.getFullName()}")
    }
}

private fun addEmployee(management: EmployeeManagement, ids: IdProvider) {
    println()
    print("Vorname: ")
    val first = readLine()?.trim().orEmpty()
    print("Nachname: ")
    val last = readLine()?.trim().orEmpty()

    if (first.isBlank() || last.isBlank()) {
        println("Vorname/Nachname dürfen nicht leer sein.")
        return
    }

    val employee = Employee(ids.generateId()).apply {
        setFirstName(first)
        setLastName(last)
        setWorkloadPercent(100u)
        setRole(Role.STAFF)
    }

    management.add(employee)
    println("Mitarbeiter hinzugefügt: ID=${employee.getId()} (${employee.getAbbreviation()})")
}

private fun deleteEmployee(management: EmployeeManagement) {
    println()
    print("Mitarbeiter-ID zum Löschen: ")
    val id = readLine()?.trim()?.toUIntOrNull()
    if (id == null) {
        println("Ungültige ID.")
        return
    }
    val ok = management.removeById(id)
    println(if (ok) "Gelöscht." else "Nicht gefunden.")
}

private fun generateVacations(year: Int, management: EmployeeManagement, vacationIds: IdProvider) {
    println()
    VacationPlanner.generateForAll(
        year = year,
        employees = management.listAll(),
        nextVacationId = { vacationIds.generateId() }
    )
    println("Ferien generiert (2-Wochen-Blöcke, Status=GEN).")
}

private fun rateSingleWeek(year: Int, management: EmployeeManagement) {
    println()
    print("Mitarbeiter-ID: ")
    val empId = readLine()?.trim()?.toUIntOrNull() ?: run {
        println("Ungültige ID.")
        return
    }

    val employee = management.findById(empId) ?: run {
        println("Mitarbeiter nicht gefunden.")
        return
    }

    print("Woche (1..52): ")
    val week = readLine()?.trim()?.toUIntOrNull() ?: run {
        println("Ungültige Woche.")
        return
    }
    if (week !in 1u..52u) {
        println("Woche muss 1..52 sein.")
        return
    }

    // finde den Eintrag, der die Woche enthält
    val entry = employee.getVacationEntries().firstOrNull { it.year == year && it.hasWeek(week) } ?: run {
        println("Für diese Woche ist kein Ferien-Eintrag vorhanden (bitte zuerst generieren).")
        return
    }

    println("Neuer Status: 1=OK (APPROVED), 2=NO (REJECTED)")
    print("Auswahl: ")
    when (ui.readChoice()) {
        1 -> {
            entry.setStatus(week, VacationStatus.APPROVED)
            println("KW $week -> OK")
        }
        2 -> {
            entry.setStatus(week, VacationStatus.REJECTED)
            println("KW $week -> NO")
        }
        else -> println("Abgebrochen.")
    }
}
