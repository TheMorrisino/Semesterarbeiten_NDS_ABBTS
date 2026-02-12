package ressourcix.gui.pages



import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.chart.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.util.Duration
import ressourcix.app.app
import ressourcix.domain.Employee
import ressourcix.logger.logger
import kotlin.math.ceil


object dashboardView : StackPane() {

    // ====================================================================================================
    // KONSTANTEN
    // ====================================================================================================
    private const val BTN_HEIGHT = 80.0
    private const val BTN_WIDTH = 200.0

    // ====================================================================================================
    // UI KOMPONENTEN
    // ====================================================================================================
    private lateinit var barChart: BarChart<String, Number>
    private lateinit var pieChart: PieChart
    private lateinit var chartContainer: VBox
    private val toggleChartButton = Button()
    private val refreshButton = Button()

    // ====================================================================================================
    // STATE
    // ====================================================================================================
    private var showingBarChart = true
    private var lastData: List<Int> = emptyList()
    private var toolTippON = true

    init {
        initializeCharts()

        val mainLayout = createMainLayout()
        children.add(mainLayout)

            Platform.runLater {
                refreshCurrentChart()
                logger.info("Dashboard initial geladen")
            }
    }

    // ====================================================================================================
    // INITIALISIERUNG
    // ====================================================================================================

    private fun initializeCharts() {
        // BarChart
        val xAxis = CategoryAxis().apply {
            label = "Kalenderwochen"
            side = Side.BOTTOM
        }
        val yAxis = NumberAxis().apply {
            label = "Anzahl MA"
            side = Side.LEFT
            minorTickCount = 0
            isAutoRanging = true
        }

        barChart = BarChart(xAxis, yAxis).apply {
            animated = false
            isLegendVisible = false
        }

        // PieChart
        pieChart = PieChart().apply {
            animated = true
            title = "Geplante/Verfügbare Ferien von Mitarbeitern"
            isLegendVisible = false
            legendSide = Side.TOP
        }
    }

    private fun createMainLayout(): GridPane {
        return GridPane().apply {
            padding = Insets(5.0)
            hgap = 1.0
            vgap = 1.0


            columnConstraints.add(ColumnConstraints().apply {
                percentWidth = 100.0
                hgrow = Priority.ALWAYS
            })
            rowConstraints.addAll(
                RowConstraints().apply {
                    percentHeight = 95.0
                    vgrow = Priority.ALWAYS
                },
                RowConstraints().apply {
                    percentHeight = 5.0
                    vgrow = Priority.ALWAYS
                }
            )
            add(createChartContainer(), 0, 0)
            add(createButtonBox(), 0, 1)
        }
    }

    private fun createChartContainer(): VBox {
        chartContainer = VBox().apply {
            spacing = 0.0
            padding = Insets(5.0)
            style = "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;"
            prefWidth = Double.MAX_VALUE
            prefHeight = Double.MAX_VALUE


            barChart.prefWidthProperty().bind(widthProperty())
            barChart.prefHeightProperty().bind(heightProperty())
            pieChart.prefWidthProperty().bind(widthProperty())
            pieChart.prefHeightProperty().bind(heightProperty())


            children.add(barChart)
            VBox.setVgrow(barChart, Priority.ALWAYS)
        }
        return chartContainer
    }

    private fun createButtonBox(): HBox {
        var box = HBox().apply {
            spacing = 10.0
            padding = Insets(5.0)
            alignment = Pos.CENTER
        }

        toggleChartButton.apply {
            text = "Zu Kuchendiagramm wechseln"
            prefHeight = BTN_HEIGHT
            prefWidth = BTN_WIDTH

            style = "-fx-font-weight: bold;"
            setOnAction { toggleChart() }
        }

        children.addAll(toggleChartButton, refreshButton)

        return box
    }

    // ====================================================================================================
    // CHART WECHSEL
    // ====================================================================================================

    private fun toggleChart() {
        showingBarChart = !showingBarChart

        Platform.runLater {
            // Cleanup
            chartContainer.children.forEach { VBox.setVgrow(it, null) }
            chartContainer.children.clear()

            // Chart wechseln
            if (showingBarChart) {
                showBarChart()
            } else {
                showPieChart()
            }

            chartContainer.layout()
        }
    }

    private fun showBarChart() {
        chartContainer.children.add(barChart)
        VBox.setVgrow(barChart, Priority.ALWAYS)
        toggleChartButton.text = "Zu Kuchendiagramm wechseln"

        lastData = emptyList() // Force update
        updateBarChart()
    }

    private fun showPieChart() {
        chartContainer.children.add(pieChart)
        VBox.setVgrow(pieChart, Priority.ALWAYS)
        toggleChartButton.text = "Zu Balkendiagramm wechseln"

        updatePieChart()
    }

    // ====================================================================================================
    // REFRESH
    // ====================================================================================================

     fun refreshCurrentChart() {
        if (showingBarChart) {
            lastData = emptyList()
            updateBarChart()
            updatePieChart()
            logger.info("BarChart & PieChart aktualisiert")
        }
    }

    // ====================================================================================================
    // BARCHART UPDATE
    // ====================================================================================================

    private fun updateBarChart() {
        val weekDetails = try {
            computeWeeklyDetails(app.employees)
        } catch (e: Exception) {
            logger.warn("Fehler beim Berechnen: $e")
            return
        }

        val counts = weekDetails.map { it.count }
        if (counts == lastData) return
        lastData = counts
        Platform.runLater {
            barChart.data.clear()
            val series = XYChart.Series<String, Number>().apply {
                name = "2026"
                weekDetails.forEachIndexed { idx, info ->
                    data.add(XYChart.Data("${idx + 1}", info.count))
                }
            }
            barChart.data.add(series)
            updateYAxis(counts)
            if (toolTippON) {
                addBarChartTooltips(series, weekDetails)
            } else {
                logger.info("Tooltips ausgeschaltet")
            }
        }
    }

    private fun updateYAxis(counts: List<Int>) {
        Platform.runLater {
            val maxValue = counts.maxOrNull() ?: 1
            (barChart.yAxis as NumberAxis).apply {
                isAutoRanging = false
                lowerBound = 0.0
                upperBound = ceil((maxValue + 1).toDouble())
                tickUnit = 1.0
            }
        }
    }

    private fun addBarChartTooltips(
        series: XYChart.Series<String, Number>,
        weekDetails: List<WeekInfo>
    ) {
        series.data.forEachIndexed { idx, point ->
            val info = weekDetails[idx]
            val tooltip = createBarChartTooltip(point.xValue, info)
            point.node?.let { Tooltip.install(it, tooltip) }
        }
    }

    private fun createBarChartTooltip(week: String, info: WeekInfo): Tooltip {
        val text = buildString {
            appendLine("KW $week")
            appendLine("Anzahl: ${info.count}")

            if (info.employees.isNotEmpty()) {
                appendLine()
                appendLine("Mitarbeiter:")
                info.employees.forEach { appendLine("  • $it") }
            }
        }

        return Tooltip(text.trim()).apply {
            showDelay = Duration.millis(100.0)
            showDuration = Duration.seconds(120.0)
        }
    }

    // ====================================================================================================
    // PIE CHART UPDATE
    // ====================================================================================================

    private fun updatePieChart() {
        val stats = try {
            computeVacationStats(app.employees)
        } catch (e: Exception) {
            logger.error("Fehler beim Berechnen: $e")
            return
        }

        Platform.runLater {
            pieChart.data.clear()

            // Daten erstellen
            val data = FXCollections.observableArrayList(
                PieChart.Data("Geplante Ferienwochen (${stats.used})", stats.used.toDouble()),
                PieChart.Data("Verfügbare Ferienwochen (${stats.available})", stats.available.toDouble())
            )
            pieChart.data = data

            // Tooltips hinzufügen
            if (toolTippON) {
            Platform.runLater {
                addPieChartTooltips(data, stats)
            }} else
                logger.info("PieChart Tooltips ausgeschaltet")

        }
    }

    private fun addPieChartTooltips(data: List<PieChart.Data>, stats: VacationStats) {
        // Tooltip für "Geplant"
        val usedTooltip = createPieTooltip("Geplante Ferienwochen", stats.used, stats.withVacation)
        data[0].node?.let { Tooltip.install(it, usedTooltip) }

        // Tooltip für "Verfügbar"
        val availableTooltip = createPieTooltip("Verfügbare Ferienwochen", stats.available, stats.withoutVacation)
        data[1].node?.let { Tooltip.install(it, availableTooltip) }
    }

    private fun createPieTooltip(title: String, count: Int, employees: List<String>): Tooltip {
        val text = buildString {
            appendLine("$title: $count")

            if (employees.isNotEmpty()) {
                appendLine()
                appendLine("Mitarbeiter:")
                employees.forEach { appendLine("  • $it") }
            }
        }

        return Tooltip(text.trim()).apply {
            showDelay = Duration.millis(100.0)
            showDuration = Duration.seconds(120.0)
        }
    }

    // ====================================================================================================
    // DATEN BERECHNUNG
    // ====================================================================================================

    private fun computeWeeklyDetails(employees: List<Employee>): List<WeekInfo> {
        val weekData = MutableList(52) { mutableListOf<String>() }

        employees.forEach { emp ->
            emp.vacationEntries.forEach { entry ->
                for (week in entry.range.startWeek..entry.range.endWeek) {
                    val index = (week - 1u).toInt()
                    if (index in 0..51) {
                        weekData[index].add(emp.getFullName())
                    }
                }
            }
        }

        return weekData.map { empList ->
            WeekInfo(
                count = empList.size,
                employees = empList.sorted()
            )
        }
    }

    private fun computeVacationStats(employees: List<Employee>): VacationStats {
        var totalUsed = 0
        var totalAvailable = 0

        val withVacation = mutableListOf<String>()
        val withoutVacation = mutableListOf<String>()

        employees.forEach { emp ->
            val limit = emp.getVacationLimit().toInt()

            // Geplante Wochen zählen
            val plannedWeeks = mutableSetOf<UInt>()
            emp.vacationEntries.forEach { entry ->
                for (week in entry.range.startWeek..entry.range.endWeek) {
                    plannedWeeks.add(week)
                }
            }

            val used = plannedWeeks.size
            val available = maxOf(0, limit - used)

            totalUsed += used
            totalAvailable += available

            // Kategorisieren
            if (used > 0) {
                val text = "${emp.getFullName()}: $used/$limit Wochen" +
                        if (available > 0) " (noch $available verfügbar)" else ""
                withVacation.add(text)
            } else {
                withoutVacation.add("${emp.getFullName()}: 0/$limit Wochen")
            }
        }

        return VacationStats(
            used = totalUsed,
            available = totalAvailable,
            withVacation = withVacation.sorted(),
            withoutVacation = withoutVacation.sorted()
        )
    }

    // ====================================================================================================
    // DATA CLASSES
    // ====================================================================================================

    private data class WeekInfo(
        val count: Int,
        val employees: List<String>
    )

    private data class VacationStats(
        val used: Int,
        val available: Int,
        val withVacation: List<String>,
        val withoutVacation: List<String>
    )
}