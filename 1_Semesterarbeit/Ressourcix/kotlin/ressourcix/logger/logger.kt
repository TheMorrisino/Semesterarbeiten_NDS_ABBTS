package ressourcix.logger

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Zentrales Logging-System für Ressourcix
 *
 * Verwendung:
 * Logger.info("Anwendung gestartet")
 * Logger.error("Fehler beim Laden", exception)
 * Logger.debug("Mitarbeiter geladen: ${employee.name}")
 */
object logger {

    // Log-Level
    enum class Level {
        DEBUG,   // Detaillierte Informationen für Entwicklung
        INFO,    // Allgemeine Informationen
        WARN,    // Warnungen
        ERROR,   // Fehler
        FATAL    // Kritische Fehler
    }

    // Konfiguration
    private var currentLevel: Level = Level.INFO
    private var logToFile: Boolean = true
    private var logToConsole: Boolean = true
    private val logDir = File("logs")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private val fileDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // In-Memory Log-Speicher (für UI-Anzeige)
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()
    private val maxBufferSize = 1000

    init {
        // Log-Verzeichnis erstellen, falls nicht vorhanden
        if (logToFile && !logDir.exists()) {
            logDir.mkdirs()
        }
    }

    data class LogEntry(
        val timestamp: LocalDateTime,
        val level: Level,
        val message: String,
        val exception: Throwable? = null,
        val threadName: String = Thread.currentThread().name,
        val className: String? = null
    ) {
        fun toFormattedString(): String {
            val time = timestamp.format(dateFormatter)
            val thread = "[$threadName]"
            val lvl = level.name.padEnd(5)
            val cls = className?.let { "[$it]" } ?: ""
            val msg = message
            val ex = exception?.let { "\n${it.stackTraceToString()}" } ?: ""
            return "$time $thread $lvl $cls $msg$ex"
        }
    }


    fun setLevel(level: Level) {
        currentLevel = level
        info("Log-Level gesetzt auf: $level")
    }


    fun setFileLogging(enabled: Boolean) {
        logToFile = enabled
    }


    fun setConsoleLogging(enabled: Boolean) {
        logToConsole = enabled
    }

    // ====================================================================================
    // Log-Methoden
    // ====================================================================================

    fun debug(message: String, exception: Throwable? = null) {
        log(Level.DEBUG, message, exception)
    }

    fun info(message: String, exception: Throwable? = null) {
        log(Level.INFO, message, exception)
    }

    fun warn(message: String, exception: Throwable? = null) {
        log(Level.WARN, message, exception)
    }

    fun error(message: String, exception: Throwable? = null) {
        log(Level.ERROR, message, exception)
    }

    fun fatal(message: String, exception: Throwable? = null) {
        log(Level.FATAL, message, exception)
    }

    private fun log(level: Level, message: String, exception: Throwable? = null) {
        // Prüfen ob Level aktiv ist
        if (level.ordinal < currentLevel.ordinal) {
            return
        }

        // Klassenname ermitteln (aus StackTrace)
        val className = try {
            Thread.currentThread().stackTrace
                .firstOrNull {
                    !it.className.contains("logger") &&
                            !it.className.contains("java.lang.Thread")
                }?.className?.split(".")?.last()
        } catch (e: Exception) {
            null
        }

        val entry = LogEntry(
            timestamp = LocalDateTime.now(),
            level = level,
            message = message,
            exception = exception,
            className = className
        )

        // Zu Buffer hinzufügen
        addToBuffer(entry)

        // Console-Output
        if (logToConsole) {
            printToConsole(entry)
        }

        // File-Output
        if (logToFile) {
            writeToFile(entry)
        }
    }

    private fun addToBuffer(entry: LogEntry) {
        logBuffer.offer(entry)

        // Buffer-Größe limitieren
        while (logBuffer.size > maxBufferSize) {
            logBuffer.poll()
        }
    }

    // ====================================================================================
    // Gibt Log auf der Console aus
    // ====================================================================================
    private fun printToConsole(entry: LogEntry) {
        val output = entry.toFormattedString()

        when (entry.level) {
            Level.ERROR, Level.FATAL -> System.err.println(output)
            else -> println(output)
        }
    }

    // ====================================================================================
    // Schreibt die Logs in Datei
    // ====================================================================================
    private fun writeToFile(entry: LogEntry) {
        try {
            val today = LocalDateTime.now().format(fileDateFormatter)
            val logFile = File(logDir, "ressourcix_$today.log")

            logFile.appendText(entry.toFormattedString() + "\n")
        } catch (e: Exception) {
            System.err.println("Fehler beim Schreiben der Log-Datei: ${e.message}")
        }
    }
//
//    // ====================================================================================
//    // Log-Abruf für UI
//    // ====================================================================================
    fun getLastLogMessage(): String? {
        return logBuffer.lastOrNull()?.message
    }

    // ====================================================================================
    // Gibt den letzten Log-Eintrag zurück (komplett)
    // ====================================================================================
    fun getLastLogEntry(): LogEntry? {
        return logBuffer.lastOrNull()
    }
}