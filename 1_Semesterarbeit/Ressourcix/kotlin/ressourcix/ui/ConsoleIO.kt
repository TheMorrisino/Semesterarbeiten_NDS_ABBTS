package ressourcix.ui

/**
 * Kapselt Console-Ein-/Ausgabe + sichere Eingaben.
 * Vorteil: weniger Duplikate, weniger Bugs.
 */
class ConsoleIO {
    fun println(msg: String = "") = kotlin.io.println(msg)
    fun print(msg: String) = kotlin.io.print(msg)

    fun readTrimmed(): String = readLine()?.trim().orEmpty()

    fun readUInt(prompt: String, min: UInt? = null, max: UInt? = null): UInt? {
        while (true) {
            print(prompt)
            val v = readTrimmed()
            if (v.isBlank()) return null
            val n = v.toUIntOrNull()
            if (n == null) {
                println("Bitte eine ganze Zahl eingeben.")
                continue
            }
            if (min != null && n < min) {
                println("Zahl muss >= $min sein.")
                continue
            }
            if (max != null && n > max) {
                println("Zahl muss <= $max sein.")
                continue
            }
            return n
        }
    }

    fun readNonBlank(prompt: String): String? {
        print(prompt)
        val s = readTrimmed()
        return if (s.isBlank()) null else s
    }

    fun readChoice(prompt: String = "Auswahl: "): Int {
        print(prompt)
        return readTrimmed().toIntOrNull() ?: -1
    }
}
