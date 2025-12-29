package ressourcix.util

/**
 * Einfacher ID-Generator.
 * start=11u bedeutet: erste ausgegebene ID ist 11.
 */
class IdProvider(start: UInt = 1u) {
    private var next: UInt = start

    fun generateId(): UInt = next++

    override fun toString(): String = "IdProvider(next=$next)"
}
