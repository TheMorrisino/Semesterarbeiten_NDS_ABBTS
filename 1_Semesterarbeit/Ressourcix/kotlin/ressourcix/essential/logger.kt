package ressourcix.logger

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue


object logger {


    private object logConfig {
        const val MAX_BUFFER_SIZE = 1000
        const val LOG_DIR_NAME = "logs"
        const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
        const val FILE_DATE_FORMAT = "yyyy-MM-dd"
        const val FILE_NAME_PREFIX = "ressourcix_"
        const val FILE_EXTENSION = ".log"
    }

    enum class Level {
        DEBUG,   // Detaillierte Informationen für Entwicklung
        INFO,    // Allgemeine Informationen
        WARN,    // Warnungen
        ERROR,   // Fehler
        FATAL;   // Kritische Fehler

        fun isLoggable(currentLevel: Level): Boolean = this.ordinal >= currentLevel.ordinal
    }

    data class LogEntry(
        val timestamp: LocalDateTime,
        val level: Level,
        val message: String,
        val exception: Throwable? = null,
        val threadName: String = Thread.currentThread().name,
        val className: String? = null
    ) {
        fun toFormattedString(): String = buildString {
            append(timestamp.format(timestampFormatter))
            append(" [").append(threadName).append("]")
            append(" ").append(level.name.padEnd(5))
            className?.let { append(" [").append(it).append("]") }
            append(" ").append(message)
            exception?.let { append("\n").append(it.stackTraceToString()) }
        }
    }

    private var currentLevel: Level = Level.INFO
    private var logToFile: Boolean = true
    private var logToConsole: Boolean = true

    private val logDir = File(logConfig.LOG_DIR_NAME)
    private val logBuffer = ConcurrentLinkedQueue<LogEntry>()
    private val logChangeListeners = mutableListOf<(LogEntry) -> Unit>()


    private val timestampFormatter = DateTimeFormatter.ofPattern(logConfig.TIMESTAMP_FORMAT)
    private val fileDateFormatter = DateTimeFormatter.ofPattern(logConfig.FILE_DATE_FORMAT)


    init {
        ensureLogDirectoryExists()
    }

    private fun ensureLogDirectoryExists() {
        if (logToFile && !logDir.exists()) {
            logDir.mkdirs()
        }
    }

    fun setLevel(level: Level) {
        currentLevel = level
        info("Log-Level gesetzt auf: $level")
    }

    fun setFileLogging(enabled: Boolean) {
        logToFile = enabled
        if (enabled) ensureLogDirectoryExists()
    }

    fun setConsoleLogging(enabled: Boolean) {
        logToConsole = enabled
    }

    fun debug(message: String, exception: Throwable? = null) =
        log(Level.DEBUG, message, exception)

    fun info(message: String, exception: Throwable? = null) =
        log(Level.INFO, message, exception)

    fun warn(message: String, exception: Throwable? = null) =
        log(Level.WARN, message, exception)

    fun error(message: String, exception: Throwable? = null) =
        log(Level.ERROR, message, exception)

    fun fatal(message: String, exception: Throwable? = null) =
        log(Level.FATAL, message, exception)

    fun addLogChangeListener(listener: (LogEntry) -> Unit) {
        synchronized(logChangeListeners) {
            logChangeListeners.add(listener)
        }
    }

    fun removeLogChangeListener(listener: (LogEntry) -> Unit) {
        synchronized(logChangeListeners) {
            logChangeListeners.remove(listener)
        }
    }

    private fun log(level: Level, message: String, exception: Throwable?) {
        if (!level.isLoggable(currentLevel)) return

        val entry = createLogEntry(level, message, exception)

        addToBuffer(entry)

        if (logToConsole) outputToConsole(entry)
        if (logToFile) outputToFile(entry)

        notifyListeners(entry)
    }

    private fun createLogEntry(level: Level, message: String, exception: Throwable?) = LogEntry(
        timestamp = LocalDateTime.now(),
        level = level,
        message = message,
        exception = exception,
        className = extractCallerClassName()
    )

    private fun addToBuffer(entry: LogEntry) {
        logBuffer.offer(entry)
        trimBufferIfNeeded()
    }

    private fun trimBufferIfNeeded() {
        while (logBuffer.size > logConfig.MAX_BUFFER_SIZE) {
            logBuffer.poll()
        }
    }

    private fun outputToConsole(entry: LogEntry) {
        val output = entry.toFormattedString()
        val stream = if (entry.level in setOf(Level.ERROR, Level.FATAL)) System.err else System.out
        stream.println(output)
    }

    private fun outputToFile(entry: LogEntry) {
        runCatching {
            val logFile = getCurrentLogFile()
            logFile.appendText(entry.toFormattedString() + "\n")
        }.onFailure { exception ->
            System.err.println("Fehler beim Schreiben der Log-Datei: ${exception.message}")
        }
    }

    private fun getCurrentLogFile(): File {
        val today = LocalDateTime.now().format(fileDateFormatter)
        return File(logDir, "${logConfig.FILE_NAME_PREFIX}$today${logConfig.FILE_EXTENSION}")
    }

    private fun notifyListeners(entry: LogEntry) {
        val listeners = synchronized(logChangeListeners) { logChangeListeners.toList() }

        listeners.forEach { listener ->
            runCatching {
                listener(entry)
            }.onFailure { exception ->
                System.err.println("Fehler beim Benachrichtigen eines Log-Listeners: ${exception.message}")
            }
        }
    }

    private fun extractCallerClassName(): String? = runCatching {
        Thread.currentThread().stackTrace
            .firstOrNull {
                !it.className.contains("logger") &&
                        !it.className.contains("java.lang.Thread")
            }?.className?.substringAfterLast('.')
    }.getOrNull()
}