package ressourcix.essential

import java.io.File
import ressourcix.app.app
import ressourcix.domain.config
import ressourcix.logger.logger

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
        val configFile = jsonDir.resolve("config.json")

        try {
            // Alle Mitarbeitenden holen
            val employees = app.management.employees.toList()

            // JSON Manuell bilden
            val json = buildString {
                appendLine("{")
                // ---------- Mitarbeitende ----------
                appendLine("  \"employees\": [")
                employees.forEachIndexed { index, emp ->
                    appendLine("    {")
                    appendLine("      \"id\": ${emp.getId()},")
                    appendLine("      \"firstName\": \"${escapeJson(emp.getFirstName())}\",")
                    appendLine("      \"lastName\": \"${escapeJson(emp.getLastName())}\",")
                    appendLine("      \"workloadPercent\": ${emp.getWorkloadPercent()},")
                    appendLine("      \"role\": \"${emp.getRole().name}\",")
                    appendLine("      \"department\": \"${emp.getDepartment()?.name ?: ""}\",")
                    appendLine("      \"education\": \"${emp.getEducation()?.name ?: ""}\",")
                    appendLine("      \"birthday\": \"${escapeJson(emp.getBirthdayAsString())}\",")
                    appendLine("      \"city\": \"${escapeJson(emp.getCity())}\",")
                    appendLine("      \"vacationLimit\": ${emp.getVacationLimit()},")

                    appendLine("      \"vacationEntries\": [")
                    val entries = emp.getVacationEntries()
                    entries.forEachIndexed { vIdx, vac ->
                        appendLine("        {")
                        appendLine("          \"id\": ${vac.id},")
                        appendLine("          \"employeeId\": ${vac.employeeId},")
                        appendLine("          \"year\": ${vac.year},")
                        appendLine("          \"startWeek\": ${vac.range.startWeek},")
                        appendLine("          \"endWeek\": ${vac.range.endWeek},")

                        appendLine("          \"weekStatus\": {")
                        val weeks = (vac.range.startWeek..vac.range.endWeek).toList()
                        weeks.forEachIndexed { wIdx, week ->
                            val status = vac.getStatus(week)?.name ?: "GENERATED"
                            append("            \"$week\": \"$status\"")
                            if (wIdx < weeks.size - 1) appendLine(",") else appendLine()
                        }
                        appendLine("          }")
                        append("        }")
                        if (vIdx < entries.size - 1) appendLine(",") else appendLine()
                    }
                    appendLine("      ]")
                    append("    }")
                    if (index < employees.size - 1) appendLine(",") else appendLine()
                }
                appendLine("  ],")   // Ende employees‑Array


                appendLine("  \"idProviders\": {")


                val empState = IdState(
                    nextId = app.employeeIds.getNextId(),
                    issuedIds = app.employeeIds.getIssuedIds().toList()
                )
                appendLine("    \"employeeIds\": {")
                appendLine("      \"nextId\": ${empState.nextId},")
                appendLine("      \"issuedIds\": ${empState.issuedIds}")
                appendLine("    },")


                val vacState = IdState(
                    nextId = app.vacationIds.getNextId(),
                    issuedIds = app.vacationIds.getIssuedIds().toList()
                )
                appendLine("    \"vacationIds\": {")
                appendLine("      \"nextId\": ${vacState.nextId},")
                appendLine("      \"issuedIds\": ${vacState.issuedIds}")
                appendLine("    }")

                appendLine("  }")
                appendLine("}")
            }

            targetFile.writeText(json)

            val configJson = buildString {
                appendLine("{")
                appendLine("  \"minEmployeeNumber\": ${config.minEmployeeNumber},")
                appendLine("  \"minApprenticeNumber\": ${config.minApprenticeNumber},")
                appendLine("  \"minManagerNumber\": ${config.minManagerNumber},")
                appendLine("  \"vacation25Years\": ${config.vacation25Years},")
                appendLine("  \"vacationOver25Years\": ${config.vacationOver25Years},")
                appendLine("  \"vacationOver50Years\": ${config.vacationOver50Years},")

                // Vacation Block als Array
                appendLine("  \"vacationBlock\": [")
                config.vacationBlock.forEachIndexed { index, value ->
                    append("    $value")
                    if (index < config.vacationBlock.size - 1) appendLine(",")
                    else appendLine()
                }
                appendLine("  ],")

                // Vacation School Block als Array
                appendLine("  \"vacationSchoolBlock\": [")
                config.vacationSchoolBlock.forEachIndexed { index, value ->
                    append("    $value")
                    if (index < config.vacationSchoolBlock.size - 1) appendLine(",")
                    else appendLine()
                }
                appendLine("  ]")
                appendLine("}")
            }
            configFile.writeText(configJson)

            logger.info("JSON erfolgreich geschrieben nach: ${targetFile.absolutePath}")
            logger.info("Config gespeichert nach: ${configFile.absolutePath}")
            println("${employees.size} Mitarbeiter exportiert")
            println("Konfiguration gespeichert")

        } catch (e: Exception) {
            logger.fatal("Fehler beim Schreiben von JSON: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}