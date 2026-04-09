// Autor Tiago & Pedro

package ressourcix.domain

enum class Role (val displayName: String) {
    APPRENTICE("Lernende"),
    STAFF("Mitarbeitende"),
    TEAM_LEAD("Teamleitung"),
    MANAGER("Management");

    override fun toString(): String = displayName
}

enum class VacationStatus { REQUESTED, APPROVED, REJECTED }

enum class Department(val displayName: String) {
    AUSSENDIENST("Aussendienst"),
    ADMIN("Admin"),
    PLANUNG("Planung");

    override fun toString(): String = displayName
}

enum class Education(val displayName: String) {
    LEHRLING("Lehrling"),
    EFZ("EFZ"),
    DIPLOM_PFLEGE_HF("dipl. Pflegefachfrau HF");

    override fun toString(): String = displayName
}

val VacationStatus.code: String
    get() = when (this) {
        VacationStatus.REQUESTED -> "\uD83D\uDD52"
        VacationStatus.APPROVED  -> "✅"
        VacationStatus.REJECTED  -> "❌"
    }

