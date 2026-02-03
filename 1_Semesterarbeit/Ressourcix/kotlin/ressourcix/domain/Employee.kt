package ressourcix.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import kotlin.text.format


class Employee(private val id: UInt) {
    private var firstName: String = ""
    private var lastName: String = ""
    private var workloadPercent: UByte = 100u
    private var role: Role = Role.STAFF
    var Abbreviation: String = ""
    private val vacationEntries: MutableList<VacationEntry> = mutableListOf()
    var vacationList: MutableList<Int> = MutableList(52) { 0 }
    private var department: Department? = null
    private var education: Education? = null
    private var birthday: LocalDate? = null
    private var city: String = ""

    private val birthdayFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.uuuu")
            .withResolverStyle(ResolverStyle.STRICT)

    fun getId(): UInt = id
    fun getFirstName(): String = firstName
    fun getLastName(): String = lastName
    fun getWorkloadPercent(): UByte = workloadPercent
    fun getRole(): Role = role
    fun getVacationEntries(): List<VacationEntry> = vacationEntries.toList()
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
        birthday = LocalDate.parse(text, birthdayFormatter) // wirft Exception bei ungültig
    }


    fun getFullName(): String =
        listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    fun abbreviationSting(): String {
        if (firstName.isBlank() || lastName.isBlank()) return ""
        Abbreviation = (firstName.take(2) + lastName.take(2)).uppercase()
        return Abbreviation
    }

    fun addVacationEntry(entry: VacationEntry) {
        createVacationList(entry)
        vacationEntries.add(entry)
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

    fun removeByStartWeek(startWeek: UInt): Boolean {
        return vacationEntries.removeIf {
            it.range.startWeek == startWeek
        }
    }


    fun createVacationList(entry: VacationEntry) {

        var startweek = entry.range.startWeek
        var endweek = entry.range.endWeek
        //println(vacationList)
        for (e in 1..52) {
            if (e >= startweek.toInt() && (e <= endweek.toInt())) {
                vacationList[e-1] = 1
                //println(vacationList)

            }

        }


    }
    fun getBirthdayAsString(): String = birthday?.format(birthdayFormatter).orEmpty()
}

fun Employee.label(): String =
    abbreviationSting().ifBlank { getFullName().ifBlank { getId().toString() } }
