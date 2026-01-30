package ressourcix.app

//import Graphical
import ressourcix.domain.EmployeeManagement
import ressourcix.ui.ConsoleIO
import ressourcix.ui.menu.mainMenu
import ressourcix.util.IdProvider

object app  {

    val io = ConsoleIO()
    val management = EmployeeManagement()
    var employees = management.employees


    private val employeeIds = IdProvider(start = 11u)
    val vacationIds = IdProvider(start = 1u)


    fun run() {
        // Seed-Daten
        management.seed10Employees()

        mainMenu.init(
            io = io,
            management = management,
            employeeIds = employeeIds,
            vacationIds = vacationIds
        )

        mainMenu.loop()
    }
}
