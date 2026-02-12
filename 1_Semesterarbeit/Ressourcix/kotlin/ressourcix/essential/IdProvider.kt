package ressourcix.essential

import ressourcix.logger.logger


data class IdState(
    val nextId: UInt,
    val issuedIds: List<UInt>
)

class IdProvider(
    var start: UInt,
) {

    private var nextId: UInt = start
    private var id: UInt = 0u
    private val issuedIds = mutableSetOf<UInt>(0u)

    @Synchronized
    fun generateId(): UInt {
        if (nextId == UInt.MAX_VALUE) {
            logger.fatal("No more IDs available")
            throw IllegalStateException("No more IDs available")
        }

        // Suche die erste freie ID
        while (nextId in issuedIds) {
            nextId++
        }
        id = nextId++
        issuedIds.add(id)
        // nextId wurde bereits erhöht – wir reduzieren wieder um 1, damit
        // der nächste Aufruf wieder bei der korrekten Stelle startet
        nextId--
        return id
    }

    fun getIssuedIds(): Set<UInt> = issuedIds.toSet()
    fun getNextId(): UInt = nextId
    fun isIssued(id: UInt): Boolean = id in issuedIds


    // Für das JASON File
    fun restore(state: IdState) {
        nextId = state.nextId
        issuedIds.clear()
        issuedIds.addAll(state.issuedIds)
    }
}