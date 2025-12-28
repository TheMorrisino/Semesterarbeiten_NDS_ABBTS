/**
 * Qualifikation eines Mitarbeiters.
 */
data class Qualification(val name: String) {
    init {
        require(name.isNotBlank()) { "Qualification name must not be blank." }
    }
}
