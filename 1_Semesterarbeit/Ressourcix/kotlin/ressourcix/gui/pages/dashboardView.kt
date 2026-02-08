package ressourcix.gui.pages

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.chart.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import ressourcix.app.app
import ressourcix.domain.Employee
import ressourcix.domain.label
import ressourcix.logger.logger


object dashboardView : StackPane() {

    private const val BTN_HEIGHT = 80.0
    private const val BTN_WIDTH = 200.0
    private const val TFL_HEIGHT = 30.0
    private const val TFL_WIDTH = 300.0

    private var barChart: BarChart<String, Number>
    private  var pieChart: PieChart
    private  var chartContainer: VBox






    private val xAxis = CategoryAxis().apply {
        label = "Kalenderwochen"
        side = javafx.geometry.Side.BOTTOM
    }
    private val yAxis = NumberAxis().apply {
        label = "Anzahl MA"
        side = javafx.geometry.Side.LEFT
        minorTickCount = 0
        isAutoRanging = true
    }

    var toggleChartButton = Button()
    var refreshButton = Button()

    // Aktueller Chart-Modus: true = BarChart, false = PieChart
    private var showingBarChart = true

    // Cache für die letzten Daten, um unnötige Updates zu vermeiden
    private var lastData: List<Int> = emptyList()

    init {
        // Hauptcontainer mit Grid-Layout (1x1)
        val gridPane = GridPane().apply {
            padding = Insets(5.0)
            hgap = 1.0
            vgap = 1.0

            // Spalten und Zeilen gleichmässig verteilen
            columnConstraints.add(
                ColumnConstraints().apply {
                    percentWidth = 100.0
                    hgrow = Priority.ALWAYS
                }
            )
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
        }

        // ====================================================================================================
        // Charts zuerst erstellen (ohne Bindings)
        // ====================================================================================================
        barChart = BarChart<String, Number>(xAxis, yAxis).apply {
            animated = false
            isLegendVisible = false
        }

        pieChart = PieChart().apply {
            animated = true
            title = "Geplante/Verfügbare Ferien von Mitarbeitern"
            isLegendVisible = false
            legendSide = Side.TOP
        }

        // ====================================================================================================
        // Top Area mit Charts (Container für beide Diagramme)
        // ====================================================================================================
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

            // Standardmäßig BarChart anzeigen
            children.add(barChart)
            VBox.setVgrow(barChart, Priority.ALWAYS)
        }


        val buttonBox = HBox().apply {
            spacing = 10.0
            padding = Insets(5.0)
            alignment = Pos.CENTER

            toggleChartButton  = createButton("Zu Kuchendiagramm wechseln")

            refreshButton = createButton("Aktualisieren")

            children.addAll(toggleChartButton, refreshButton)

            toggleChartButton.setOnAction {
                toggleChart()
            }

            refreshButton.setOnAction {
                refreshCurrentChart()
            }
        }




        gridPane.add(chartContainer, 0, 0)      // Oben
        gridPane.add(buttonBox, 0, 1)           // Unten

        // Grid zum StackPane hinzufügen
        children.add(gridPane)

        Platform.runLater {
            Thread.sleep(100) //
            Platform.runLater {
                refreshCurrentChart()
                logger.info("Dashboard initial geladen")
            }
        }
    }


    // ====================================================================================================
    // Wechselt zwischen BarChart und PieChart
    // ====================================================================================================
    private fun toggleChart() {
        showingBarChart = !showingBarChart
        Platform.runLater {
            chartContainer.children.forEach { child ->
                VBox.setVgrow(child, null)
            }
            chartContainer.children.clear()

            if (showingBarChart) {    // BarChart hinzufügen
                chartContainer.children.add(barChart)
                VBox.setVgrow(barChart, Priority.ALWAYS)
                toggleChartButton.text = "Zu Kuchendiagramm wechseln"
                val tempData = lastData // Cache zurücksetzen für sofortiges Update
                lastData = emptyList()
                updateBarChart()
                if (tempData.isEmpty()) {
                    lastData = emptyList() // Bei leerem Cache wird Update erzwungen
                }
            } else {
                // PieChart hinzufügen
                chartContainer.children.add(pieChart)
                VBox.setVgrow(pieChart, Priority.ALWAYS)
                toggleChartButton.text = "Zu Balkendiagramm wechseln"
                updatePieChart()
            }
            chartContainer.layout()
        }
    }

    // ====================================================================================================
    // Aktualisiert das aktuell angezeigte Diagramm
    // ====================================================================================================
    private fun refreshCurrentChart() {

        if (showingBarChart) {
            lastData = emptyList()
            updateBarChart()
            logger.info("BarChart aktualisiert")
        } else {
            updatePieChart()
            logger.info("PieChart aktualisiert")
        }
    }

    // ====================================================================================================
    // Aktualisiert das BarChart mit Mitarbeitern, die Ferien in KW Wochen haben
    // ====================================================================================================
    private var lastWeekDetails: List<WeekInfo> = emptyList()
    fun updateBarChart() {
        val weekDetails = try {
            computeWeeklyOverlapWithDetails(app.employees)
        } catch (e: UninitializedPropertyAccessException) {
            return
        }

        // Prüfe ob sich die Counts geändert haben
        val overlapCounts = weekDetails.map { it.count }
        if (overlapCounts == lastData) {
            return
        }

        lastData = overlapCounts
        lastWeekDetails = weekDetails

        Platform.runLater {
            barChart.data.clear()

            val series = XYChart.Series<String, Number>().apply {
                weekDetails.forEachIndexed { idx, weekInfo ->
                    val weekLabel = "${idx + 1}"
                    data.add(XYChart.Data(weekLabel, weekInfo.count))
                }
            }
            series.name = "2026"
            barChart.data.add(series)

            // Y-Achse konfigurieren
            Platform.runLater {
                val maxValue = if (overlapCounts.isNotEmpty()) overlapCounts.max() else 1
                yAxis.apply {
                    isAutoRanging = false
                    lowerBound = 0.0
                    upperBound = Math.ceil((maxValue + 1).toDouble())
                    tickUnit = 1.0
                }
            }

            // Tooltips mit Mitarbeiternamen
            series.data.forEachIndexed { idx, point ->
                val weekInfo = weekDetails[idx]

                val tooltipText = buildString {
                    appendLine("KW ${point.xValue}")
                    appendLine("Anzahl: ${weekInfo.count}")

                    if (weekInfo.employees.isNotEmpty()) {
                        appendLine()
                        appendLine("Mitarbeiter:")
                        weekInfo.employees.forEach { empName ->
                            appendLine("  • $empName")
                        }
                    }
                }

                val tip = Tooltip(tooltipText.trim()).apply {
                    showDelay = Duration.millis(100.0)
                    showDuration = Duration.seconds(120.0)
                }
                point.node?.let { Tooltip.install(it, tip) }
            }
        }
    }

    // ====================================================================================================
    // Aktualisiert das PieChart mit Mitarbeitern, die Ferien haben vs. die keine Ferien
    // ====================================================================================================
    fun updatePieChart() {
        val stats = try {
            computeVacationStats(app.employees)
        } catch (e: UninitializedPropertyAccessException) {
            logger.error("$e")
            return
        }

        Platform.runLater {
            pieChart.data.clear()

            val dataWithVacation = PieChart.Data(
                "Geplante Ferienwochen (${stats.withVacation})",
                stats.withVacation.toDouble()
            )

            val dataWithoutVacation = PieChart.Data(
                "Verfügbare Ferienwochen (${stats.withoutVacation})",
                stats.withoutVacation.toDouble()
            )

            val data = FXCollections.observableArrayList(
                dataWithVacation,
                dataWithoutVacation
            )

            pieChart.data = data

            // Tooltips mit Mitarbeiterlisten
            Platform.runLater {
                // Tooltip für "Mit Ferien"
                val withVacationTooltip = buildString {
                    appendLine("Geplante Ferienwochen: ${stats.withVacation}")
                    if (stats.employeesWithVacation.isNotEmpty()) {
                        appendLine()
                        appendLine("Mitarbeiter:")
                        stats.employeesWithVacation.forEach { emp ->
                            appendLine("  • $emp")
                        }
                    }
                }

                dataWithVacation.node?.let {
                    Tooltip.install(it, Tooltip(withVacationTooltip.trim()).apply {
                        showDelay = Duration.millis(100.0)
                        showDuration = Duration.seconds(120.0)
                    })
                }

                // Tooltip für "Ohne Ferien"
                val withoutVacationTooltip = buildString {
                    appendLine("Verfügbare Ferienwochen: ${stats.withoutVacation}")
                    if (stats.employeesWithoutVacation.isNotEmpty()) {
                        appendLine()
                        appendLine("Mitarbeiter ohne Ferien:")
                        stats.employeesWithoutVacation.forEach { emp ->
                            appendLine("  • $emp")
                        }
                    }
                }

                dataWithoutVacation.node?.let {
                    Tooltip.install(it, Tooltip(withoutVacationTooltip.trim()).apply {
                        showDelay = Duration.millis(100.0)
                        showDuration = Duration.seconds(120.0)
                    })
                }
            }
        }
    }

    private fun createButton(text: String): Button =
        Button(text).apply {
            prefHeight = BTN_HEIGHT
            prefWidth = BTN_WIDTH
            textAlignment = TextAlignment.CENTER
            alignment = Pos.CENTER
            isFocusTraversable = false
            style = " -fx-font-weight: bold;"
        }


    private data class WeekInfo(
        val count: Int,
        val employees: List<String>
    )


//    private fun computeWeeklyOverlap(employees: List<Employee>): List<Int> {
//        val counts = app.management.getOverlapList()
//
//        return counts
//    }

    private fun computeWeeklyOverlapWithDetails(employees: List<Employee>): List<WeekInfo> {
        val weekData = MutableList(52) { mutableListOf<String>() }

        employees.forEach { emp ->
            emp.getVacationEntries().forEach { entry ->
                for (w in entry.range.startWeek..entry.range.endWeek) {
                    val weekIndex = (w - 1u).toInt()
                    if (weekIndex in 0..51) {
                        weekData[weekIndex].add(emp.getFullName())
                    }
                }
            }
        }

        // Konvertiere zu WeekInfo-Liste
        return weekData.map { empList ->
            WeekInfo(
                count = empList.size,
                employees = empList.sorted()  // Alphabetisch sortiert
            )
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Berechnet Statistiken über Mitarbeiter mit und ohne Ferien für PieChart
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun computeVacationStats(employees: List<Employee>): VacationStats {
        var totalUsedWeeks = 0
        var totalAvailableWeeks = 0

        val empsWithVacation = mutableListOf<String>()
        val empsWithoutVacation = mutableListOf<String>()

        employees.forEach { emp ->
            val limit = emp.getVacationLimit().toInt()

            val plannedWeeks = mutableSetOf<UInt>()
            emp.getVacationEntries().forEach { entry ->
                for (week in entry.range.startWeek..entry.range.endWeek) {
                    plannedWeeks.add(week)
                }
            }

            val usedByThisEmployee = plannedWeeks.size
            totalUsedWeeks += usedByThisEmployee

            val availableByThisEmployee = maxOf(0, limit - usedByThisEmployee)
            totalAvailableWeeks += availableByThisEmployee

            // NEU: Mitarbeiter kategorisieren
            if (usedByThisEmployee > 0) {

                val remaining = limit - usedByThisEmployee
                empsWithVacation.add(
                    "${emp.getFullName()}: ${usedByThisEmployee}/${limit} Wochen" +
                            if (remaining > 0) " (noch $remaining verfügbar)" else ""
                )
            } else {
                // Hat noch keine Ferien geplant
                empsWithoutVacation.add("${emp.getFullName()}: 0/${limit} Wochen")
            }
        }

        return VacationStats(
            withVacation = totalUsedWeeks,
            withoutVacation = totalAvailableWeeks,
            total = totalUsedWeeks + totalAvailableWeeks,
            employeesWithVacation = empsWithVacation.sorted(),
            employeesWithoutVacation = empsWithoutVacation.sorted()
        )
    }

    private data class VacationStats(
        val withVacation: Int,
        val withoutVacation: Int,
        val total: Int,
        val employeesWithVacation: List<String>,
        val employeesWithoutVacation: List<String>
        )
}