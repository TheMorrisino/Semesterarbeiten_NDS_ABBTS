package ressourcix.essential


class IdProvider(var start: UInt,) {



    private var nextId: UInt = start
    private var id : UInt = 0u
    private val issuedIds = mutableSetOf<UInt>(0u)

    @Synchronized
    fun generateId(): UInt {
        if (nextId == UInt.MAX_VALUE) {
            println("Maximum number ${UInt.MAX_VALUE}")
            throw IllegalStateException("No more IDs available")

        } else {
            while (nextId in issuedIds) {
                id = nextId++ }

            //Weil der letzte durchgang nicht zählt
            id = nextId++
            issuedIds.add(id)
            nextId -= 1u
        }
        return id
    }

    /** Liefert eine Kopie der bereits vergebenen IDs. */
    fun getIssuedIds(): Set<UInt> = issuedIds.toSet()

    /** Gibt den aktuellen Zähler zurück – wird vom Writer benötigt. */
    fun getNextId(): UInt = nextId

    /** Prüft, ob eine ID bereits vergeben ist. */
    fun isIssued(id: UInt): Boolean = id in issuedIds

}