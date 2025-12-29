package ressourcix.app

import ressourcix.domain.EmployeeManagement
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.MainMenu
import ressourcix.util.IdProvider

/**
 * Composition Root:
 * Baut die Abhängigkeiten zusammen und startet das Hauptmenü.
 */
class App(
    private val io: ConsoleIO = ConsoleIO(),
    private val management: EmployeeManagement = EmployeeManagement(),
    private val employeeIds: IdProvider = IdProvider(start = 11u), // Seeds nutzen 1..10
    private val vacationIds: IdProvider = IdProvider(start = 1u),
) {
    fun run() {
        // Seed: 10 Mitarbeiter, damit du sofort testen kannst
        management.seed10Employees()

        MainMenu(
            io = io,
            management = management,
            employeeIds = employeeIds,
            vacationIds = vacationIds
        ).loop()
    }
}
