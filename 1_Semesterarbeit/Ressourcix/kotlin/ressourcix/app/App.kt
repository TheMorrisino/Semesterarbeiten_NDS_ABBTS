package ressourcix.app

import Graphical
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.MainMenu
import ressourcix.util.IdProvider

object App : Graphical {
    val io: ConsoleIO = ConsoleIO()
    val management: EmployeeManagement = EmployeeManagement()
    private val employeeIds: IdProvider = IdProvider(start = 11u)
    val vacationIds: IdProvider = IdProvider(start = 1u)

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
