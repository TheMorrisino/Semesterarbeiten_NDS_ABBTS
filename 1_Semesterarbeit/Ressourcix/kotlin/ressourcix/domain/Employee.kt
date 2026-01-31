package ressourcix.domain


class Employee(private val id: UInt) {
    private var firstName: String = ""
    private var lastName: String = ""
    private var workloadPercent: UByte = 100u
    private var role: Role = Role.STAFF
    var Abbreviation : String = ""
    private val vacationEntries: MutableList<VacationEntry> = mutableListOf()
    private var department: Department? = null
    private var education: Education? = null

    fun getId(): UInt = id
    fun getFirstName(): String = firstName
    fun getLastName(): String = lastName
    fun getWorkloadPercent(): UByte = workloadPercent
    fun getRole(): Role = role
    fun getVacationEntries(): List<VacationEntry> = vacationEntries.toList()


    fun setFirstName(value: String) {
        require(value.isNotBlank()) { "firstName must not be blank" }
        firstName = value.trim()
    }

    fun setLastName(value: String) {
        require(value.isNotBlank()) { "lastName must not be blank" }
        lastName = value.trim()
    }

    fun setWorkloadPercent(value: UByte) {
        require(value <= 100u) { "workloadPercent must be 0..100" }
        workloadPercent = value
    }

    fun setRole(value: Role) {
        role = value
    }

    fun getDepartment(): Department? = department
    fun getEducation(): Education? = education

    fun getFullName(): String =
        listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    fun abbreviationSting(): String {
        if (firstName.isBlank() || lastName.isBlank()) return ""
        Abbreviation = (firstName.take(2) + lastName.take(2)).uppercase()
        return Abbreviation
    }

    fun addVacationEntry(entry: VacationEntry) {
        vacationEntries.add(entry)
    }

}

fun Employee.label(): String =
    abbreviationSting().ifBlank { getFullName().ifBlank { getId().toString() } }
