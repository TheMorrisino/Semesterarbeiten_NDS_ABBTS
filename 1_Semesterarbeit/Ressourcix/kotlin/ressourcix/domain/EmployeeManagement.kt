package ressourcix.domain


import ressourcix.app.app.vacationIds
import ressourcix.gui.pages.calenderView
import ressourcix.gui.pages.calenderView.updateEmployees
import ressourcix.logger.logger
// Als Klasse definiert um später Verschiedene Jahre zu verwalten
class EmployeeManagement () {
    val employees: MutableList<Employee> = mutableListOf()
    private val overlapList : MutableList<Int> = MutableList(52) { 0 }

    var maxOverlapsPerKw :Int = 5
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

    fun findById(id: UInt): Employee? = employees.firstOrNull { it.getId() == id }


    fun allVacationInKwFiltern() {
        val allVacation = employees.flatMap { it.vacationEntries }

    }

    fun removeVacation(empId: UInt, startWeek: UInt, endWeek: UInt){
        employees[empId.toInt()-1].removeByStartWeek(startWeek)
    }

    fun canAddVacation(
        employee: Employee,
        startWeek: UInt,
        endWeek: UInt
    ): Boolean {

        return employee.vacationEntries.none { existing ->
            val existingStart = existing.range.startWeek
            val existingEnd = existing.range.endWeek

            // Intervall-Überschneidung
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
                //overlapList[i] = 0
                overlapList[i] += employees[e].getVacationByIndex(i)
                //println(overlapList)
        }
        //println(overlapList)
    }


    fun VacationEntry.overlapsWith(other: VacationEntry): Boolean {
        // Nur überlappen wenn gleiches Jahr
        if (this.year != other.year) return false

        // Wochen-Überschneidung prüfen
        return this.range.overlaps(other.range)
    }

    fun countAllOverlaps(): Int {
        val allVacation = employees.flatMap { it.vacationEntries }
        var count = 0

        for (i in allVacation.indices) {
            for (j in i + 1 until allVacation.size) {
                if (allVacation[i].overlapsWith(allVacation[j])) {
                    count++
                }
            }
        }

        return count
    }

    fun getEmployeesInWeek(week: Int): List<Employee> {
        return employees.filter { emp ->
            emp.getVacationByIndex(week) == 1
        }
    }

    fun getEmployeeByIndex(index: Int): Employee = employees[index]

    fun getOverlapList(): List<Int> {
        updateOverlapList()
        return overlapList
    }


}

