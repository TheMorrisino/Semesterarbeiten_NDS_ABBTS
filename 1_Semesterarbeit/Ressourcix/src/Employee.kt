class Employee(id: UInt) {

    // --- interne Felder ---

    private val id: UInt = id

    private var firstName: String = ""
    private var lastName: String = ""
    private var workloadPercent: UByte = 0u      // 0–100 %
    private var role: Role = Role.STAFF

    private val qualifications: MutableList<Qualification> = mutableListOf()
    private val vacationEntries: MutableList<VacationEntry> = mutableListOf()

    // --- GETTER ---

    fun getId(): UInt = id

    fun getFirstName(): String = firstName

    fun getLastName(): String = lastName

    fun getFullName(): String =
        listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    fun getWorkloadPercent(): UByte = workloadPercent

    fun getRole(): Role = role

    fun getQualifications(): List<Qualification> = qualifications.toList()

    fun getVacationEntries(): List<VacationEntry> = vacationEntries.toList()

    /**
     * Kürzel: 2 Buchstaben Vorname + 2 Buchstaben Nachname.
     */
    fun getAbbreviation(): String {
        if (firstName.isBlank() || lastName.isBlank()) return ""
        return (firstName.take(2) + lastName.take(2)).uppercase()
    }

    // --- SETTER ---

    fun setFirstName(value: String) {
        require(value.isNotBlank()) { "firstName must not be blank." }
        firstName = value.trim()
    }

    fun setLastName(value: String) {
        require(value.isNotBlank()) { "lastName must not be blank." }
        lastName = value.trim()
    }

    fun setWorkloadPercent(value: UByte) {
        require(value <= 100u) { "workloadPercent must be between 0 and 100." }
        workloadPercent = value
    }

    fun setRole(value: Role) {
        role = value
    }

    // --- Listen-Operationen ---

    fun addQualification(qualification: Qualification) {
        qualifications.add(qualification)
    }

    fun removeQualification(qualification: Qualification) {
        qualifications.remove(qualification)
    }

    fun addVacationEntry(entry: VacationEntry) {
        require(entry.employeeId == id) {
            "VacationEntry.employeeId (${entry.employeeId}) does not match Employee.id ($id)."
        }
        vacationEntries.add(entry)
    }

    /**
     * Wird vom Generator genutzt, um bestehende generierte Daten zu überschreiben.
     */
    fun clearVacationEntries() {
        vacationEntries.clear()
    }
}
