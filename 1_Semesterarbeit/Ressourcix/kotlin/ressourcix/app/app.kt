package ressourcix.app


import ressourcix.domain.EmployeeManagement
import ressourcix.essential.jsonReader
import ressourcix.logger.logger
import ressourcix.essential.IdProvider

object app  {

    var management = EmployeeManagement()
    var employees = management.employees
    val employeeIds = IdProvider(start = 1u)
    val vacationIds = IdProvider(start = 1u)

    fun run() {
        val loadedEmployees = jsonReader.read()
        if (loadedEmployees.isNotEmpty()) {
            management.employees.clear()
            management.employees.addAll(loadedEmployees)
            management.updateOverlapList()
            logger.info("Daten aus JSON geladen")
        }
    }
}