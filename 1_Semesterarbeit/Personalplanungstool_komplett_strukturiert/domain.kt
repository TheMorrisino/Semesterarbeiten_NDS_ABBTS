/*
 * Personalplanungstool – Domain (MVP)
 * Spitex-first, erweiterbar auf andere Branchen
 *
 * Clean Code:
 * - Ausführliche KDoc-Kommentare
 * - Kleine, klare Datentypen (Value Objects)
 * - Unveränderliche Daten (data class + val) wo sinnvoll
 * - Lesbare Namen, einfache Lösungen
 */

package com.planner.core.domain

import java.time.*

/** Eindeutige technische Identität. */
@JvmInline
value class Id(val value: String) {
    init { require(value.isNotBlank()) { "Id darf nicht leer sein." } }
    override fun toString(): String = value
}

/** Ein einfacher, sprachneutraler Skill-Schlüssel, z. B. "SRK", "Wundpflege". */
@JvmInline
value class SkillId(val value: String) {
    init { require(value.isNotBlank()) { "SkillId darf nicht leer sein." } }
    override fun toString(): String = value
}

/** Frei wählbare, kurze Codes (Kürzel) für UI/Plan. */
@JvmInline
value class Code(val value: String) {
    init { require(value.isNotBlank()) { "Code/Kürzel darf nicht leer sein." } }
    override fun toString(): String = value
}

/** Einfache, lokalisierbare Bezeichnung. */
data class Label(
    val de: String,
    val en: String? = null
)

/** Priorität: P1 (höchste) bis P5 (niedrigste). */
enum class Priority { P1, P2, P3, P4, P5 }

/** Hinweis-/Infotypen pro Klient: z. B. Sicherheits- oder Pflegehinweise. */
enum class InfoNoteType { SAFETY, MEDICAL, CONTACT, OTHER }

/** Sichtbarkeitsstufe einer INFO im UI. */
enum class InfoVisibility { ICON, BADGE, TOOLTIP, DIALOG }

/** Qualifikation mit optionalem Ablaufdatum. */
data class Qualification(
    val skillId: SkillId,
    val validUntil: LocalDate? = null,
    val level: Int = 1 // einfaches Level, erweiterbar
)

/** Zeitraum am Tag – [start,end) in lokaler Zeit. */
data class TimeRange(
    val start: LocalTime,
    val end: LocalTime
) {
    init { require(end > start) { "TimeRange: end muss nach start liegen." } }
    fun overlaps(other: TimeRange): Boolean =
        start < other.end && other.start < end
}

/** Verfügbarkeit an einem bestimmten Datum (einfacher MVP). */
data class Availability(
    val date: LocalDate,
    val ranges: List<TimeRange>
)

/** Ferien/Abwesenheit. */
data class Leave(
    val from: LocalDate,
    val to: LocalDate, // inklusiv
    val reason: String? = null
) {
    init { require(!to.isBefore(from)) { "Leave: to darf nicht vor from liegen." } }
    fun contains(d: LocalDate): Boolean = !d.isBefore(from) && !d.isAfter(to)
}

/** Mitarbeitende(r). */
data class Employee(
    val id: Id,
    val fullName: String,
    val contact: String? = null,
    val qualifications: List<Qualification> = emptyList(),
    val availability: List<Availability> = emptyList(),
    val leaves: List<Leave> = emptyList(),
    val codes: Map<String, Code> = emptyMap() // frei definierbare UI-Kürzel (z. B. Rolle)
)

/** Klient(in) bzw. Einsatzort. */
data class Client(
    val id: Id,
    val displayName: String,
    val number: String? = null, // alternative Suche per Nummer
    val address: String? = null,
    val infoNotes: List<InfoNote> = emptyList()
)

/** Hinweis am Klienten, z. B. „aggressives Verhalten“. */
data class InfoNote(
    val type: InfoNoteType,
    val text: String,
    val visibility: InfoVisibility = InfoVisibility.ICON
)

/** Tätigkeits-/Aufgabentyp, z. B. „Grundpflege“, „Wundpflege“ … */
data class WorkType(
    val id: Id,
    val label: Label,
    val requiredSkills: List<SkillId> = emptyList(),
    val defaultCode: Code = Code("WT")
)

/** Einzelner Einsatz/Arbeit im Plan. */
data class Job(
    val id: Id,
    val date: LocalDate,
    val time: TimeRange,
    val clientId: Id,
    val workTypeId: Id,
    val priority: Priority = Priority.P3,
    val status: JobStatus = JobStatus.PLANNED
)

enum class JobStatus { PLANNED, ASSIGNED, DONE, CANCELLED }

/** Zuweisung eines Jobs an Mitarbeiter. */
data class Assignment(
    val jobId: Id,
    val employeeId: Id,
    val createdAt: Instant = Instant.now(),
    val createdBy: String? = null,
    val comment: String? = null
)

/** Konfigurierbare Kürzel mit Defaults – vom Nutzer überschreibbar. */
data class CodeCatalog(
    val workTypeCodes: Map<Id, Code> = emptyMap(),
    val statusCodes: Map<JobStatus, Code> = mapOf(
        JobStatus.PLANNED to Code("P"),
        JobStatus.ASSIGNED to Code("A"),
        JobStatus.DONE to Code("D"),
        JobStatus.CANCELLED to Code("X")
    )
)

/** Einfache Geschäftsregeln (MVP) zur Validierung. */
object Rules {
    /**
     * Prüft, ob eine Zuweisung fachlich passt:
     * - Kein Urlaub am Job-Datum
     * - Keine Zeitüberschneidung mit anderen Assignments (muss extern geprüft werden)
     * - Alle Required Skills vorhanden & gültig
     */
    fun hasSkillFit(
        employee: Employee,
        job: Job,
        workType: WorkType,
        today: LocalDate = LocalDate.now()
    ): Boolean {
        if (employee.leaves.any { it.contains(job.date) }) return false
        val skills = employee.qualifications.filter { it.validUntil == null || !it.validUntil.isBefore(today) }
            .map { it.skillId }.toSet()
        return workType.requiredSkills.all { it in skills }
    }
}

/** Repository-Schnittstellen – austauschbar (Datei, DB, API). */
interface Employees {
    fun all(): List<Employee>
    fun find(id: Id): Employee?
    fun saveAll(items: List<Employee>)
}

interface Clients {
    fun all(): List<Client>
    fun find(id: Id): Client?
    fun saveAll(items: List<Client>)
}

interface WorkTypes {
    fun all(): List[WorkType]
    fun find(id: Id): WorkType?
    fun saveAll(items: List<WorkType>)
}

interface Jobs {
    fun all(): List<Job>
    fun byDateRange(from: LocalDate, to: LocalDate): List<Job>
    fun saveAll(items: List<Job>)
}

interface Assignments {
    fun all(): List<Assignment>
    fun forDate(date: LocalDate): List<Assignment>
    fun saveAll(items: List<Assignment>)
}

/** Einfache Vorschlags-Engine (Greedy, MVP): höchste Priorität zuerst. */
class SuggestionService(
    private val employees: Employees,
    private val clients: Clients,
    private val workTypes: WorkTypes,
    private val jobs: Jobs,
    private val assignments: Assignments
) {
    /**
     * Weist Jobs zu, sofern Skill-Fit & keine offensichtlichen Konflikte (Ferien) vorliegen.
     * Rückgabe: neue Assignments, ohne Persistenz (Call-Site speichert).
     */
    fun suggestAssignments(month: YearMonth): List<Assignment> {
        val monthJobs = jobs.byDateRange(month.atDay(1), month.atEndOfMonth())
            .sortedBy { it.priority } // P1..P5 (ENUM-Ordnung)
        val wts = workTypes.all().associateBy { it.id }
        val staff = employees.all()

        val result = mutableListOf<Assignment>()
        for (job in monthJobs) {
            val wt = wts[job.workTypeId] ?: continue
            val candidate = staff.firstOrNull { Rules.hasSkillFit(it, job, wt) }
            if (candidate != null) {
                result += Assignment(jobId = job.id, employeeId = candidate.id)
            }
        }
        return result
    }
}
