package ressourcix.util

class IdProvider(
    var start: UInt,


) {

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



//        println("\nDebug")
//        println("Issued IDs: $issuedIds")
//        println("Next ID to issue: $nextId")
//        println("Returned ID: $id")

        return id
    }


    fun Issued(id: UInt): Boolean = id in issuedIds
}