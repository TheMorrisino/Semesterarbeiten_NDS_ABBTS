package ressourcix.app

import essential.*
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.essential.jsonReader
import ressourcix.essential.jsonWriter
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.mainMenu
import ressourcix.util.IdProvider

object app  {

    val io = ConsoleIO()
    var management = EmployeeManagement()

    var employees = management.employees

    private val employeeIds = IdProvider(start = 11u)
    val vacationIds = IdProvider(start = 1u)


    fun run() {
        // ZUERST versuchen zu laden
        val loadedEmployees = jsonReader.read()
        if (loadedEmployees.isNotEmpty()) {
            management.employees.clear()
            management.employees.addAll(loadedEmployees)
            println("✓ Daten aus JSON geladen")
        }

        // Menu initialisieren
        mainMenu.init(
            io = io,
            management = management,
            employeeIds = employeeIds,
            vacationIds = vacationIds
        )

        // Menu-Loop starten
        mainMenu.loop()


    }
}