package ressourcix.essential

import java.io.File
import ressourcix.app.app
import ressourcix.logger.logger

//TODO ID Provider mitnehemen da nach dem laden neue id mitgenomen werden
object jsonWriter {

    private val jsonDir = File("jsonfiles")
    private var jsonToFile: Boolean = true

    init {
        if (jsonToFile && !jsonDir.exists()) {
            jsonDir.mkdirs()
        }
    }

    fun write() {
        if (!jsonToFile) {
            println("JSON-Export ist deaktiviert.")
            return
        }

        val targetFile = jsonDir.resolve("employees.json")

        try {
            val employees = app.management.employees.toList()

            // Manuell JSON erstellen
            val json = buildString {
                appendLine("[")
                employees.forEachIndexed { index, emp ->
                    appendLine("  {")
                    appendLine("    \"id\": ${emp.getId()},")
                    appendLine("    \"firstName\": \"${escapeJson(emp.getFirstName())}\",")
                    appendLine("    \"lastName\": \"${escapeJson(emp.getLastName())}\",")
                    appendLine("    \"workloadPercent\": ${emp.getWorkloadPercent()},")
                    appendLine("    \"role\": \"${emp.getRole().name}\",")
                    appendLine("    \"department\": \"${emp.getDepartment()?.name ?: ""}\",")
                    appendLine("    \"education\": \"${emp.getEducation()?.name ?: ""}\",")
                    appendLine("    \"birthday\": \"${escapeJson(emp.getBirthdayAsString())}\",")
                    appendLine("    \"city\": \"${escapeJson(emp.getCity())}\",")
                    appendLine("    \"vacationLimit\": ${emp.getVacationLimit()},")

                    // Vacation Entries
                    appendLine("    \"vacationEntries\": [")
                    val entries = emp.getVacationEntries()
                    entries.forEachIndexed { vIndex, vacation ->
                        appendLine("      {")
                        appendLine("        \"id\": ${vacation.id},")
                        appendLine("        \"employeeId\": ${vacation.employeeId},")
                        appendLine("        \"year\": ${vacation.year},")
                        appendLine("        \"startWeek\": ${vacation.range.startWeek},")
                        appendLine("        \"endWeek\": ${vacation.range.endWeek},")

                        // Status für jede Woche
                        appendLine("        \"weekStatus\": {")
                        val weeks = (vacation.range.startWeek..vacation.range.endWeek).toList()
                        weeks.forEachIndexed { wIndex, week ->
                            val status = vacation.getStatus(week)?.name ?: "GENERATED"
                            append("          \"$week\": \"$status\"")
                            if (wIndex < weeks.size - 1) appendLine(",")
                            else appendLine()
                        }
                        appendLine("        }")

                        append("      }")
                        if (vIndex < entries.size - 1) appendLine(",")
                        else appendLine()
                    }
                    appendLine("    ]")

                    append("  }")
                    if (index < employees.size - 1) appendLine(",")
                    else appendLine()
                }
                append("]")
            }

            targetFile.writeText(json)
            logger.info("JSON erfolgreich geschrieben nach: ${targetFile.absolutePath}")
            println("${employees.size} Mitarbeiter exportiert")

        } catch (e: Exception) {
            logger.fatal("Fehler beim Schreiben von JSON: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun escapeJson(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}