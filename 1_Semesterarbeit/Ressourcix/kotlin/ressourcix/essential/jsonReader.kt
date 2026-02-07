package ressourcix.essential

import java.io.File
import ressourcix.domain.*
import ressourcix.logger.logger

object jsonReader {

    private val jsonDir = File("jsonfiles")

    fun read(): List<Employee> {
        val targetFile = jsonDir.resolve("employees.json")

        if (!targetFile.exists()) {
            logger.warn("JSON-Datei nicht gefunden: ${targetFile.absolutePath}")
            return emptyList()
        }

        try {
            val json = targetFile.readText()
            val employees = mutableListOf<Employee>()

            // Parse JSON manuell
            val jsonObjects = extractJsonObjects(json)

            jsonObjects.forEach { jsonObj ->
                val fields = parseJsonObject(jsonObj)

                // Employee erstellen
                val id = fields["id"]?.toUIntOrNull() ?: return@forEach
                val employee = Employee(id)

                // Basisdaten setzen
                fields["firstName"]?.let { if (it.isNotBlank()) employee.setFirstName(it) }
                fields["lastName"]?.let { if (it.isNotBlank()) employee.setLastName(it) }
                fields["workloadPercent"]?.toUByteOrNull()?.let { employee.setWorkloadPercent(it) }
                fields["city"]?.let { employee.setCity(it) }
                fields["vacationLimit"]?.toUIntOrNull()?.let { employee.setVacationLimit(it) }
                fields["birthday"]?.let { if (it.isNotBlank()) {
                    try {
                        employee.setBirthdayFromString(it)
                    } catch (e: Exception) {
                        logger.warn("Ungültiges Geburtsdatum: $it")
                    }
                }}

                // Enum-Werte
                fields["role"]?.let { roleName ->
                    if (roleName.isNotBlank()) {
                        try {
                            employee.setRole(Role.valueOf(roleName))
                        } catch (e: Exception) {
                            logger.warn("Ungültige Rolle: $roleName")
                        }
                    }
                }
                fields["department"]?.let { deptName ->
                    if (deptName.isNotBlank()) {
                        try {
                            employee.setDepartment(Department.valueOf(deptName))
                        } catch (e: Exception) {
                            logger.warn("Ungültiges Department: $deptName")
                        }
                    }
                }
                fields["education"]?.let { eduName ->
                    if (eduName.isNotBlank()) {
                        try {
                            employee.setEducation(Education.valueOf(eduName))
                        } catch (e: Exception) {
                            logger.warn("Ungültige Bildung: $eduName")
                        }
                    }
                }

                // Vacation Entries parsen
                fields["vacationEntries"]?.let { vacationJson ->
                    val vacationObjects = extractJsonObjects(vacationJson)
                    vacationObjects.forEach { vacObj ->
                        val vacFields = parseJsonObject(vacObj)
                        val vacId = vacFields["id"]?.toUIntOrNull() ?: return@forEach
                        val employeeId = vacFields["employeeId"]?.toUIntOrNull() ?: return@forEach
                        val year = vacFields["year"]?.toUIntOrNull() ?: return@forEach
                        val startWeek = vacFields["startWeek"]?.toUIntOrNull() ?: return@forEach
                        val endWeek = vacFields["endWeek"]?.toUIntOrNull() ?: return@forEach

                        val range = WeekRange(startWeek, endWeek)
                        val entry = VacationEntry(vacId, employeeId, year, range)

                        // Status für jede Woche setzen
                        vacFields["weekStatus"]?.let { statusJson ->
                            val statusMap = parseJsonObject(statusJson.removePrefix("{").removeSuffix("}"))
                            statusMap.forEach { (weekStr, statusStr) ->
                                val week = weekStr.toUIntOrNull() ?: return@forEach
                                try {
                                    val status = VacationStatus.valueOf(statusStr)
                                    entry.setStatus(week, status)
                                } catch (e: Exception) {
                                    logger.warn("Ungültiger Status: $statusStr für Woche $week")
                                }
                            }
                        }

                        employee.addVacationEntry(entry)
                    }
                }

                employees.add(employee)
            }
            // WICHTIG: Nach dem Laden alle VacationLists neu berechnen (Sicherheit)
            employees.forEach { emp ->
                emp.createVacationList()
            }

            logger.info("${employees.size} Mitarbeiter geladen")
            println("${employees.size} Mitarbeiter importiert")
            return employees

        } catch (e: Exception) {
            logger.fatal("Fehler beim Lesen von JSON: ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun extractJsonObjects(json: String): List<String> {
        val objects = mutableListOf<String>()
        var depth = 0
        var start = -1

        json.forEachIndexed { index, char ->
            when (char) {
                '{' -> {
                    if (depth == 0) start = index
                    depth++
                }
                '}' -> {
                    depth--
                    if (depth == 0 && start != -1) {
                        objects.add(json.substring(start, index + 1))
                        start = -1
                    }
                }
            }
        }
        return objects
    }

    private fun parseJsonObject(jsonObj: String): Map<String, String> {
        val fields = mutableMapOf<String, String>()

        val content = jsonObj.trim().removePrefix("{").removeSuffix("}")

        var currentKey = ""
        var currentValue = StringBuilder()
        var inString = false
        var inArray = false
        var inObject = false
        var arrayDepth = 0
        var objectDepth = 0
        var escaped = false

        var i = 0
        while (i < content.length) {
            val char = content[i]

            when {
                escaped -> {
                    currentValue.append(char)
                    escaped = false
                }
                char == '\\' -> {
                    escaped = true
                }
                char == '"' && !escaped -> {
                    inString = !inString
                }
                char == '[' && !inString -> {
                    inArray = true
                    arrayDepth++
                    currentValue.append(char)
                }
                char == ']' && !inString -> {
                    arrayDepth--
                    if (arrayDepth == 0) inArray = false
                    currentValue.append(char)
                }
                char == '{' && !inString -> {
                    inObject = true
                    objectDepth++
                    currentValue.append(char)
                }
                char == '}' && !inString -> {
                    objectDepth--
                    if (objectDepth == 0) inObject = false
                    currentValue.append(char)
                }
                char == ':' && !inString && !inArray && !inObject && currentKey.isEmpty() -> {
                    currentKey = currentValue.toString().trim().trim('"')
                    currentValue.clear()
                }
                char == ',' && !inString && !inArray && !inObject -> {
                    if (currentKey.isNotEmpty()) {
                        fields[currentKey] = currentValue.toString().trim().trim('"')
                        currentKey = ""
                        currentValue.clear()
                    }
                }
                else -> {
                    if (inString || inArray || inObject || (!char.isWhitespace() || currentValue.isNotEmpty())) {
                        currentValue.append(char)
                    }
                }
            }
            i++
        }

        if (currentKey.isNotEmpty()) {
            fields[currentKey] = currentValue.toString().trim().trim('"')
        }

        return fields
    }
}