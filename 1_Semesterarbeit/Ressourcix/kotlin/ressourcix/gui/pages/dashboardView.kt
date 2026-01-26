package ressourcix.gui.pages

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Side
import javafx.scene.chart.*
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import ressourcix.domain.Employee
import ressourcix.domain.VacationEntry
import ressourcix.gui.GuiBorderPane.Companion.graphical
import javafx.util.Duration


object dashboardView : StackPane() {

    private lateinit var barChart: BarChart<String, Number>
    private val xAxis = CategoryAxis().apply {
        label = "Kalenderwochen"
        side = javafx.geometry.Side.BOTTOM
    }
    private val yAxis = NumberAxis().apply {
        label = "Anzahl MA"
        side = javafx.geometry.Side.LEFT
    }

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
                    percentHeight = 100.0
                    vgrow = Priority.ALWAYS
                },
                RowConstraints().apply {
                    percentHeight = 0.0
                    vgrow = Priority.ALWAYS
                }
            )
        }

        // ====================================================================================================
        // Top Area mit BarChart
        // ====================================================================================================
        val topArea = VBox().apply {
            spacing = 0.0
            padding = Insets(5.0)
            style = "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;"

            // BarChart erstellen und als Klassenvariable speichern
            barChart = BarChart<String, Number>(xAxis, yAxis).apply {
//              title = "Übersicht"
                animated = true
                isLegendVisible = false

            }

            children.add(barChart)
            prefWidth = Double.MAX_VALUE
            prefHeight = Double.MAX_VALUE
            VBox.setVgrow(barChart, Priority.ALWAYS)
        }

        val empty = VBox()

        // Alle Bereiche zum Grid hinzufügen
        gridPane.add(topArea, 0, 0)      // Oben Links
        gridPane.add(empty, 0, 1)        // Unten Links

        // Grid zum StackPane hinzufügen
        children.add(gridPane)

        // Initial Chart befüllen
        updateBarChart()
    }

    /**
     * Aktualisiert das BarChart mit aktuellen Daten
     */
    fun updateBarChart() {
        // Alte Daten entfernen
        barChart.data.clear()

        // Neue Daten berechnen
        val overlapCounts = computeWeeklyOverlap(graphical.employees)

        // Neue Series erstellen
        val series = XYChart.Series<String, Number>().apply {
            overlapCounts.forEachIndexed { idx, cnt ->
                val weekLabel = "${idx + 1}"
                data.add(XYChart.Data(weekLabel, cnt))
            }
        }

        // Series zum Chart hinzufügen
        barChart.data.add(series)

        // Tooltips für jeden Datenpunkt hinzufügen
        series.data.forEach { point ->
            val tip = Tooltip("KW ${point.xValue}\nAnzahl: ${point.yValue}").apply {
                showDelay = Duration.millis(100.0)
            }
            point.node?.let { Tooltip.install(it, tip) }
        }
    }

    private fun computeWeeklyOverlap(employees: List<Employee>): List<Int> {
        // 52 Plätze, initial 0
        val counts = MutableList(52) { 0 }

        employees.forEach { emp ->
            emp.getVacationEntries()
                .forEach { entry ->
                    for (w in entry.range.startWeek..entry.range.endWeek) {
                        counts[(w - 1u).toInt()]++
                    }
                }
        }
        return counts
    }
}