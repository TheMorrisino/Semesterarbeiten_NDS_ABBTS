package ressourcix.domain

enum class Role { APPRENTICE, STAFF, TEAM_LEAD, MANAGER }

enum class VacationStatus { REQUESTED, APPROVED, GENERATED, REJECTED, CANCELLED }

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
        VacationStatus.REQUESTED -> "REQ"
        VacationStatus.APPROVED  -> "APP"
        VacationStatus.GENERATED -> "GEN"
        VacationStatus.REJECTED  -> "REJ"
        VacationStatus.CANCELLED -> "CAN"
    }
