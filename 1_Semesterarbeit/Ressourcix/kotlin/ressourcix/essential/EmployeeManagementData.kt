package ressourcix.essential

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

@Serializable
data class EmployeeManagementData(
    val year: UInt,
    val employees: List<EmployeeData>,
    val overlapList: List<Int>
)

@Serializable
data class EmployeeData(
    val id: UInt,
    val firstName: String,
    val lastName: String,
    val workloadPercent: UInt,
    val role: String,
    val vacations: List<VacationEntryData> = emptyList()
)

@Serializable
data class VacationEntryData(
    val id: UInt,
    val employeeId: UInt,
    val year: UInt,
    val startWeek: UInt,
    val endWeek: UInt,
    val status: String
)

private val xml = XML { indent = 4 }

fun saveToXml(file: File, data: EmployeeManagementData) {
    val text = xml.encodeToString(EmployeeManagementData.serializer(), data)
    file.writeText(text)
}

fun loadFromXml(file: File): EmployeeManagementData {
    return xml.decodeFromString(EmployeeManagementData.serializer(), file.readText())
}

