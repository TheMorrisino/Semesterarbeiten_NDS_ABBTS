package ressourcix.app

import essential.*
import javafx.application.Platform
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.essential.jsonReader
import ressourcix.essential.jsonWriter
import ressourcix.gui.pages.dashboardView
import ressourcix.logger.logger
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.mainMenu
import ressourcix.util.IdProvider

object app  {

    val io = ConsoleIO()
    var management = EmployeeManagement()

    var employees = management.employees

    val employeeIds = IdProvider(start = 1u)
    val vacationIds = IdProvider(start = 1u)


    fun run() {
        // ZUERST versuchen zu laden
        val loadedEmployees = jsonReader.read()
        if (loadedEmployees.isNotEmpty()) {
            management.employees.clear()
            management.employees.addAll(loadedEmployees)
            management.updateOverlapList()
            logger.info("Daten aus JSON geladen")
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