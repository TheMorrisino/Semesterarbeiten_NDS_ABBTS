package ressourcix.app

import Graphical
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.MainMenu
import ressourcix.util.IdProvider

class App(
    private val io: ConsoleIO = ConsoleIO(),
    val management: EmployeeManagement = EmployeeManagement(),
    private val employeeIds: IdProvider = IdProvider(start = 11u),
    private val vacationIds: IdProvider = IdProvider(start = 1u),
) : Graphical {

    override var employees = management.employees

    fun run() {
        management.seed10Employees()

        MainMenu(
            io = io,
            management = management,
            employeeIds = employeeIds,
            vacationIds = vacationIds
        ).loop()


    }
}
