package com.planner.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalDate
import java.time.YearMonth

// --- einfache Domain (MVP) ---
enum class Priority { P1, P2, P3, P4, P5 }
enum class JobStatus { PLANNED, ASSIGNED, DONE, CANCELLED }

data class WorkType(
    val id: String,
    val label: String,
    val requiredSkills: List<String>,
    val code: String
)
data class Client(
    val id: String,
    val name: String,
    val infoIcon: Boolean
)
data class Job(
    val id: String,
    val date: LocalDate,
    val start: String,
    val end: String,
    val clientId: String,
    val workTypeId: String,
    val priority: Priority,
    val status: JobStatus
)
data class Employee(
    val id: String,
    val name: String,
    val skills: Set<String>,
    val leaves: List<ClosedRange<LocalDate>>
)

data class Repo(
    val employees: List<Employee>,
    val workTypes: Map<String, WorkType>,
    val clients: Map<String, Client>,
    val jobs: List<Job>
)

fun loadCsvRepo(folder: File): Repo {
    val reader = csvReader { delimiter = ',' }

    val employees = run {
        val f = File(folder, "employees.csv")
        if (!f.exists()) emptyList() else
            reader.readAllWithHeader(f).map { row ->
                val skills = row["qualifications"]
                    ?.split(';')?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
                val leaves = (row["leaves"] ?: "")
                    .split(';')
                    .filter { it.contains("..") }
                    .map {
                        val (s, e) = it.split("..")
                        LocalDate.parse(s)..LocalDate.parse(e)
                    }
                Employee(
                    id = row["id"]!!.trim(),
                    name = row["full_name"]!!.trim(),
                    skills = skills,
                    leaves = leaves
                )
            }
    }

    val workTypes = run {
        val f = File(folder, "work_types.csv")
        if (!f.exists()) emptyMap() else
            reader.readAllWithHeader(f).associate { row ->
                val id = row["id"]!!.trim()
                val required = (row["required_skills"] ?: "")
                    .split(';').filter { it.isNotBlank() }
                val code = (row["default_code"] ?: "WT").trim()
                id to WorkType(
                    id = id,
                    label = row["label_de"]?.ifBlank { id } ?: id,
                    requiredSkills = required,
                    code = code
                )
            }
    }

    val clients = run {
        val f = File(folder, "clients.csv")
        if (!f.exists()) emptyMap() else
            reader.readAllWithHeader(f).associate { row ->
                val infoNotes = (row["info_notes"] ?: "[]")
                val showIcon = infoNotes.contains("SAFETY", true)
                        || infoNotes.contains("aggressiv", true)
                val id = row["id"]!!.trim()
                id to Client(id = id, name = row["display_name"]!!.trim(), infoIcon = showIcon)
            }
    }

    val jobs = run {
        val f = File(folder, "jobs.csv")
        if (!f.exists()) emptyList() else
            reader.readAllWithHeader(f).map { row ->
                Job(
                    id = row["id"]!!.trim(),
                    date = LocalDate.parse(row["date"]!!.trim()),
                    start = row["start"]!!.trim(),
                    end = row["end"]!!.trim(),
                    clientId = row["client_id"]!!.trim(),
                    workTypeId = row["work_type_id"]!!.trim(),
                    priority = Priority.valueOf(row["priority"]?.trim() ?: "P3"),
                    status = JobStatus.valueOf(row["status"]?.trim() ?: "PLANNED")
                )
            }
    }

    return Repo(employees, workTypes, clients, jobs)
}

data class FilterState(
    val workTypeId: String? = null,
    val employeeId: String? = null,
    val priority: Priority? = null
)

@Composable
fun App() {
    val dataDir = File("data")
    val repo by remember { mutableStateOf(loadCsvRepo(dataDir)) }
    var month by remember { mutableStateOf(YearMonth.now()) }
    var filter by remember { mutableStateOf(FilterState()) }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Button(onClick = { month = month.minusMonths(1) }) { Text("‹") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { month = YearMonth.now() }) { Text("Heute") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { month = month.plusMonths(1) }) { Text("›") }
                        }
                        Text("Planner – Monatsansicht $month")
                    }
                }
            }
        ) { _ ->
            MonthGrid(
                month = month,
                jobs = repo.jobs,
                clients = repo.clients,
                workTypes = repo.workTypes,
                filter = filter
            )
        }
    }

}

@Composable
fun MonthGrid(
    month: YearMonth,
    jobs: List<Job>,
    clients: Map<String, Client>,
    workTypes: Map<String, WorkType>,
    filter: FilterState
) {
    // Start der Kalender-Ansicht: Montag in der Woche der Monats-1
    val firstOfMonth = month.atDay(1)
    val start = firstOfMonth.minusDays(((firstOfMonth.dayOfWeek.value - 1).toLong()))
    val end = month.atEndOfMonth()
    val byDate = jobs.filter { it.date >= start && it.date <= end.plusDays(6) }.groupBy { it.date }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        for (week in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val day = start.plusDays((week * 7L) + col)
                    val inSameMonth = day.month == month.month
                    val entries = byDate[day] ?: emptyList()

                    Card(
                        elevation = 2.dp,
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(day.dayOfMonth.toString(), style = MaterialTheme.typography.subtitle2)
                            entries.forEach { j ->
                                val wt = workTypes[j.workTypeId]
                                val cl = clients[j.clientId]
                                val info = if (cl?.infoIcon == true) " ⚠" else ""
                                Text(
                                    "- ${j.start}-${j.end} ${cl?.name ?: j.clientId} [${wt?.code ?: "WT"}]$info",
                                    style = MaterialTheme.typography.body2
                                )
                            }
                            if (!inSameMonth) {
                                Text("(außerhalb Monat)", style = MaterialTheme.typography.caption)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Planner Desktop MVP") {
        App()
    }
}
