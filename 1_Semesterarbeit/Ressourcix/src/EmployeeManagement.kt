/**
 * Verwaltung aller Mitarbeiter.
 *
 * Clean-Code: zentrale CRUD-Funktionen, interne Liste bleibt gekapselt.
 */
class EmployeeManagement {
    private val employees: MutableList<Employee> = mutableListOf()

    init {
        // "Datenbank": 10 Start-Mitarbeiter
        seedEmployees()
    }

    private fun seedEmployees() {
        // IDs 1..10
        val names = listOf(
            "Anna" to "Bucher",
            "Cem" to "Demir",
            "Eva" to "Frei",
            "Luca" to "Meyer",
            "Noah" to "Keller",
            "Mia" to "Huber",
            "Lea" to "Schmid",
            "Jonas" to "Weber",
            "Sara" to "Koch",
            "Tim" to "Steiner"
        )
        println(names)

        names.forEachIndexed { index, (first, last) ->
            val id = (index + 1).toUInt()
            val e = Employee(id).apply {
                setFirstName(first)
                setLastName(last)
                setWorkloadPercent(100u)
                setRole(Role.STAFF)
            }
            employees.add(e)
        }
    }

    fun add(employee: Employee) {
        require(employees.none { it.getId() == employee.getId() }) {
            "Employee with id ${employee.getId()} already exists."
        }
        employees.add(employee)
    }

    fun removeById(id: UInt): Boolean = employees.removeIf { it.getId() == id }

    fun findById(id: UInt): Employee? = employees.firstOrNull { it.getId() == id }

    fun findByAbbreviation(abbr: String): Employee? = employees.firstOrNull { it.getAbbreviation() == abbr }

    fun listAll(): List<Employee> = employees.toList()
}
