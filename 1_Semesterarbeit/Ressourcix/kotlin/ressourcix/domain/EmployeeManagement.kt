// Autor Morris & Tiago & Pedro

package ressourcix.domain

import ressourcix.app.app.vacationIds
import ressourcix.gui.pages.calenderView.updateEmployees

// Als Klasse definiert um später Verschiedene Jahre zu verwalten
class EmployeeManagement () {
    val employees: MutableList<Employee> = mutableListOf()
    private val overlapList : MutableList<Int> = MutableList(52) { 0 }

    var maxOverlapsPerKw :Int = 5 // Wird später verwendet
    val year = 2026u

    fun listAll(): List<Employee> = employees.toList()

    fun add(employee: Employee) {
        employees.add(employee)
        updateEmployees()
    }

    fun removeById(id: UInt): Boolean {
        val idx = employees.indexOfFirst { it.getId() == id }
        if (idx == -1) return false
        employees.removeAt(idx)
        updateEmployees()
        return true
    }

    fun removeVacation(empId: UInt, startWeek: UInt): Boolean{
        employees.find {it.getId() == empId}.apply { this!!.removeByStartWeek(startWeek)}
        return true
    }

    fun canAddVacation(
        employee: Employee,
        startWeek: UInt,
        endWeek: UInt
    ): Boolean {

        return employee.vacationEntries.none { existing ->
            val existingStart = existing.range.startWeek
            val existingEnd = existing.range.endWeek

            startWeek <= existingEnd && endWeek >= existingStart
        }
    }

    fun addVacationSafe(
        employee: Employee,
        startWeek: UInt,
        endWeek: UInt
    ): Boolean {

        if (!canAddVacation(employee, startWeek, endWeek)) {
            return false
        }

        val entry = VacationEntry(
            id = vacationIds.generateId(),
            employeeId = employee.getId(),
            year = year,
            range = WeekRange(startWeek, endWeek),
            status = VacationStatus.REQUESTED
        )

        employee.addVacationEntry(entry)
        updateOverlapList()

        return true
    }

    fun updateOverlapList (){
        overlapList.replaceAll { 0 }
        for (i in 0 .. 51)
            for (e in 0 .. employees.size - 1){
                overlapList[i] += employees[e].getVacationByIndex(i)
        }
    }


    fun VacationEntry.overlapsWith(other: VacationEntry): Boolean {
        if (this.year != other.year) return false

        return this.range.overlaps(other.range)
    }

    fun getOverlapList(): List<Int> {
        updateOverlapList()
        return overlapList
    }


}

