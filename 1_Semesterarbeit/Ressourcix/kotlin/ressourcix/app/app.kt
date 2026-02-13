package ressourcix.app


import ressourcix.domain.EmployeeManagement
import ressourcix.essential.jsonReader
import ressourcix.logger.logger
import ressourcix.essential.IdProvider
import ressourcix.gui.bottomBar.updateStatusIfChanged

object app  {

    var management = EmployeeManagement()
    var employees = management.employees
    val employeeIds = IdProvider(start = 1u)
    val vacationIds = IdProvider(start = 1u)
    var jasonFileAktiv: Boolean= true



    fun run() {

        // Config laden
        jsonReader.readConfig()
        logger.debug("Daten aus Config.jason geladen")

        // JASON employees FILE LADEN
        if (jasonFileAktiv) {
            val loadedEmployees = jsonReader.read()
            if (loadedEmployees.isNotEmpty()) {
                management.employees.clear()
                management.employees.addAll(loadedEmployees)
                management.updateOverlapList()
                logger.debug("Daten aus employees.jason geladen")
            } else
                logger.debug("JASON File konnte nicht geladen werden")
        }
        logger.debug("JASON File ausgeschaltet")

        while (true) {
            try {
                updateStatusIfChanged()
            } catch (e: InterruptedException) {
                break
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    }