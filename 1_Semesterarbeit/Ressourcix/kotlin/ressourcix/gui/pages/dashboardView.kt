package ressourcix.gui.pages

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.chart.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import ressourcix.domain.Employee.*
import ressourcix.domain.VacationEntry
import javafx.util.Duration
import ressourcix.app.app
import ressourcix.domain.Employee
import ressourcix.domain.EmployeeManagement
import ressourcix.gui.GuiBorderPane
import ressourcix.logger.logger


object dashboardView : StackPane() {



    private lateinit var barChart: BarChart<String, Number>
    private lateinit var pieChart: PieChart
    private lateinit var chartContainer: VBox

    private val employees = app.employees





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

    val toggleChartButton = Button()
    val refreshButton = Button()

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
            title = "Mitarbeiter mit und ohne Ferien"
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

            toggleChartButton.text = "Zu Kuchendiagramm wechseln"
            refreshButton.text = "Aktualisieren"

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
            // Entferne alle Kinder und VGrow-Einstellungen
            chartContainer.children.forEach { child ->
                VBox.setVgrow(child, null)
            }
            chartContainer.children.clear()

            if (showingBarChart) {
                // BarChart hinzufügen
                chartContainer.children.add(barChart)
                VBox.setVgrow(barChart, Priority.ALWAYS)
                toggleChartButton.text = "Zu Kuchendiagramm wechseln"
                // Cache zurücksetzen für sofortiges Update
                val tempData = lastData
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

            // Layout neu berechnen
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
    fun updateBarChart() {
        val overlapCounts = try {
            computeWeeklyOverlap(app.employees)

        } catch (e: UninitializedPropertyAccessException) {
            return
        }

        if (overlapCounts == lastData) {
            return
        }

        lastData = overlapCounts

        Platform.runLater {
            barChart.data.clear()
            // Neue Series erstellen
            val series = XYChart.Series<String, Number>().apply {

                overlapCounts.forEachIndexed { idx, cnt ->
                    val weekLabel = "${idx + 1}"
                    data.add(XYChart.Data(weekLabel, cnt))
                }

            }
            series.name = "2026"

            // Series zum Chart hinzufügen
            barChart.data.add(series)

            // Y-Achse mit 0.5er-Schritten konfigurieren, nachdem Daten geladen sind
            Platform.runLater {
                val maxValue = if (overlapCounts.isNotEmpty()) overlapCounts.max() else 1
                yAxis.apply {
                    isAutoRanging = false
                    lowerBound = 0.0
                    upperBound = Math.ceil((maxValue + 1).toDouble())
                    tickUnit = 1.0
                }
            }

            // Tooltips für jeden Datenpunkt hinzufügen
            series.data.forEach { point ->
                val tip = Tooltip("KW ${point.xValue}\nAnzahl: ${point.yValue}").apply {
                    showDelay = Duration.millis(100.0)
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

            val data = FXCollections.observableArrayList(
                PieChart.Data("Mit Ferien (${stats.withVacation})", stats.withVacation.toDouble()),
                PieChart.Data("Ohne Ferien (${stats.withoutVacation})", stats.withoutVacation.toDouble())
            )

            pieChart.data = data
        }
    }



    private fun computeWeeklyOverlap(employees: List<Employee>): List<Int> {
        val counts = app.management.getOverlapList()

        return counts
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Berechnet Statistiken über Mitarbeiter mit und ohne Ferien
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun computeVacationStats(employees: List<Employee>): VacationStats {
        var totalUsedWeeks = 0
        var totalAvailableWeeks = 0

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
        }

        return VacationStats(
            withVacation = totalUsedWeeks,
            withoutVacation = totalAvailableWeeks,
            total = totalUsedWeeks + totalAvailableWeeks
        )
    }

    private data class VacationStats(
        val withVacation: Int,
        val withoutVacation: Int,
        val total: Int
    )
}