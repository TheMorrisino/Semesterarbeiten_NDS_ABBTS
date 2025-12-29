package ressourcix.domain

enum class Role { APPRENTICE, STAFF, TEAM_LEAD, MANAGER }

enum class VacationStatus { REQUESTED, APPROVED, GENERATED, REJECTED, CANCELLED }

val VacationStatus.code: String
    get() = when (this) {
        VacationStatus.REQUESTED -> "REQ"
        VacationStatus.APPROVED  -> "APP"
        VacationStatus.GENERATED -> "GEN"
        VacationStatus.REJECTED  -> "REJ"
        VacationStatus.CANCELLED -> "CAN"
    }
