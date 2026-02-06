package ressourcix.essential

import java.beans.XMLEncoder
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.nio.file.Paths
import java.io.File
import ressourcix.app.app
import ressourcix.logger.logger


object xmlWriter {

    private val xmlDir = File("xmlfiles")
    private var xmlToFile: Boolean = true

    private val calendarXml = app.allEmployee

    init {

        if (xmlToFile && !xmlDir.exists()) {
            xmlDir.mkdirs()
        }
    }

    @JvmStatic
    fun write() {
        if (!xmlToFile) {
            println("XML‑Export ist deaktiviert.")
            return
        }

        // Zieldatei innerhalb des Ordners
        val targetFile = xmlDir.resolve("calendarXml.xml")

        try {
            // XMLEncoder wird in einem use‑Block geöffnet → automatisches Schließen
            XMLEncoder(
                BufferedOutputStream(
                    FileOutputStream(targetFile)
                )
            ).use { encoder ->
                encoder.writeObject(calendarXml)

            }
            logger.info("XML erfolgreich geschrieben nach: ${targetFile.absolutePath}")
        } catch (e: Exception) {
            logger.fatal("Fehler beim Schreiben von XML: ${e.message}")
                   }
    }
}


