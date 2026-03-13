// Autor Morris & Tiago & Pedro

package ressourcix.domain

import ressourcix.logger.logger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class Employee(private val id: UInt) {
    private var firstName: String = ""
    private var lastName: String = ""
    private var workloadPercent: UByte = 100u
    private var role: Role = Role.APPRENTICE
    private var abbreviation: String = ""
    val vacationEntries: MutableList<VacationEntry> = mutableListOf()
    private val vacationEntryIds : MutableList<UInt> = mutableListOf()
    private var vacationList: MutableList<Int> = MutableList(52) { 0 }
    private var department: Department? = null
    private var education: Education? = null
    private var birthday: LocalDate? = null
    private var city: String = ""
    private var vacationLimit : UInt = 5u // Anzahl Ferien nur über get und set
    var plannedVacation = vacationList.sum().toUInt()

    private val birthdayFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT)

    fun getId(): UInt = id

    fun vacationStatus(): String {
        val planned = vacationList.sum()
        val remaining = vacationLimit.toInt() - planned

        return when {
            remaining > 0  -> " [$planned/$vacationLimit] ⏳"
            remaining == 0 -> " [$planned/$vacationLimit] ✔"
            else           -> " [$planned/$vacationLimit] ✖"
        }
    }

    fun getFirstName(): String = firstName
    fun getLastName(): String = lastName
    fun getWorkloadPercent(): UByte = workloadPercent
    fun getRole(): Role = role
    fun getDepartment(): Department? = department
    fun getEducation(): Education? = education
    fun getBirthday(): LocalDate? = birthday
    fun getCity(): String = city

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

    fun setDepartment(value: Department?) { department = value }
    fun setEducation(value: Education?) { education = value }
    fun setCity(value: String) {
        city = value.trim()
    }

    fun setBirthdayFromString(value: String) {
        val text = value.trim()
        if (text.isBlank()) {
            birthday = null
            return
        }
        birthday = LocalDate.parse(text, birthdayFormatter)
    }


    fun getFullName(maxLength: Int = 12): String {
        val full = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        if (full.length <= maxLength) {
            return full
        }

        val firstInitial = firstName.firstOrNull()?.toString() ?: ""
        val remainingLength =
            maxLength - firstInitial.length - 1 // 1 für Leerzeichen

        val shortenedLastName =
            lastName.take(remainingLength.coerceAtLeast(0))

        return listOf(firstInitial, shortenedLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    fun abbreviationSting(): String {
        if (firstName.isBlank() || lastName.isBlank()) return ""
        abbreviation = (firstName.take(2) + lastName.take(2)).uppercase()
        return abbreviation
    }

    fun addVacationEntry(entry: VacationEntry) {
        vacationEntries.add(entry)
        vacationEntryIds.add(entry.id)
        createVacationList()
        plannedVacation = vacationList.sum().toUInt()
        logger.info("Ferieneintrag hinzugefügt Startwoche ${entry.range.startWeek} bis ${entry.range.endWeek} von ${getFullName(99)}")
    }

    fun removeVacationEntry(vacationId: UInt, entry: VacationEntry){
        vacationEntries.removeIf { it.id == vacationId }
    }

    fun getIdWithStartWeek(startWeek: UInt): UInt? {
        for (entry in vacationEntries) {
            if (entry.range.startWeek == startWeek) {
                return entry.id
            }
        }
        return null
    }

    fun findEntryByStartweek (startWeek: UInt): Boolean {
        vacationEntries.find {
            it.range.startWeek == startWeek
        } ?: return false
        return true
    }

    fun removeByStartWeek(startWeek: UInt): Boolean {
        val entry = vacationEntries.find {
            it.range.startWeek == startWeek
        } ?: return false
        vacationEntries.remove(entry)
        vacationEntryIds.remove(entry.id)
        createVacationList()
        logger.info("Ferieneintrag gelöscht Startwoche ${entry.range.startWeek} bis ${entry.range.endWeek} von ${getFullName(99)}")
        plannedVacation = vacationList.sum().toUInt()
        return true
    }

    fun getVacationByIndex(index: Int) :  Int {
         return vacationList[index]
    }

    fun createVacationList() {
        vacationList = MutableList(52) { 0 }
        for (vacation in vacationEntries){
            for (e in 1..52) {
                if (e >= vacation.range.startWeek.toInt() && (e <= vacation.range.endWeek.toInt())) {
                    vacationList[e - 1] = 1

                }
            }
        }
    }

    fun getBirthdayAsString(): String = birthday?.format(birthdayFormatter).orEmpty()

    fun getAbbreviation(): String = abbreviation

    fun setVacationLimit(limit: UInt){
      vacationLimit = limit
    }

    fun getVacationLimit() = vacationLimit
}

fun Employee.label(): String =
    abbreviationSting().ifBlank { getFullName().ifBlank { getId().toString() } }


