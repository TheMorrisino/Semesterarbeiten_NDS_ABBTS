package ressourcix.calendar

import ressourcix.domain.Employee

interface CalendarOutput {
    fun printYearPlan(year: UInt, employees: List<Employee>)
}
