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
import ressourcix.domain.Employee
import ressourcix.domain.VacationEntry
//import ressourcix.gui.GuiBorderPane.Companion.graphical
import javafx.util.Duration
import ressourcix.app.app
import ressourcix.gui.GuiBorderPane



object dashboardView : StackPane() {



    private lateinit var barChart: BarChart<String, Number>
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

    val thankYouButton = Button()


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
        // Top Area mit BarChart
        // ====================================================================================================
        val topArea = VBox().apply {
            spacing = 0.0
            padding = Insets(5.0)
            style = "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;"

            // BarChart erstellen und als Klassenvariable speichern
            barChart = BarChart<String, Number>(xAxis, yAxis).apply {
                animated = false
                isLegendVisible = false

            }

            children.add(barChart)
            prefWidth = Double.MAX_VALUE
            prefHeight = Double.MAX_VALUE
            VBox.setVgrow(barChart, Priority.ALWAYS)
        }



        val empty = VBox()
        empty.apply {
            thankYouButton.setText("Thank You")
            children.add(thankYouButton)
            alignment = Pos.BOTTOM_CENTER


            thankYouButton.setOnAction{
                alert()
            }



        }




        gridPane.add(topArea, 0, 0)      // Oben
        gridPane.add(empty, 0, 1)        // Unten

        // Grid zum StackPane hinzufügen
        children.add(gridPane)


        // Initial Chart befüllen wird später gemacht, wenn graphical initialisiert ist
        // updateBarChart() wird vom Update-Thread aufgerufen
    }





    fun alert()  {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Information"
            headerText = "Dankeschön"
            contentText = "Vielen Dank, dass Sie unsere Software nutzen!\nIhr Ressourcix‑Team \uD83D\uDE80"
        }.showAndWait()
    }


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