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
        // ====================================================================================================
        // ===== OBEN : Linechart-Diagramm =====
        val topArea = VBox().apply {
            spacing = 0.0
            padding = Insets(5.0)
            style = "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;"

            // Titel
            val titleLabel = Label("Abwesenheiten").apply {
                font = Font.font("System", FontWeight.BOLD, 20.0)
            }
//            children.add(titleLabel)

            // Linechart erstellen
            val xAxis = CategoryAxis().apply {
                label = "Kalenderwochen"
                side = Side.BOTTOM
            }
            val yAxis = NumberAxis().apply {
                label = "Anzahl MA"
                side = Side.LEFT

            }
            val linechart = LineChart<String, Number>(xAxis, yAxis).apply {
                // Chart soll den verfügbaren Platz nutzen
                animated = true
                createSymbols = true
                isLegendVisible = false


//                title = "Übersicht"
                // Beispieldaten - hier kannst du deine eigenen Daten eintragen
                val series = XYChart.Series<String, Number>().apply {
                    val overlapCounts = computeWeeklyOverlap(graphical.employees)
                    overlapCounts.forEachIndexed { idx, cnt ->
                        val weekLabel = "${idx + 1}"
                        data.add(XYChart.Data(weekLabel, cnt))

                    }

                }


                data.add(series)
                series.data.forEach { point ->
                    val tip = Tooltip("KW ${point.xValue}\nAnzahl: ${point.yValue}")
                    // Tooltip sofort anzeigen, wenn die Maus darüber fährt
                    tip.showDelay = Duration.millis(100.0)
                    point.node?.let { Tooltip.install(it, tip) }
                }



            }
            children.add(linechart)
            prefWidth = Double.MAX_VALUE
            prefHeight = Double.MAX_VALUE
            VBox.setVgrow(linechart, Priority.ALWAYS)
        }

        // ====================================================================================================
        // ====================================================================================================
        // ===== UNTEN LINKS: Warnungen und Hinweise =====
        val bottomArea = VBox().apply {
            spacing = 0.0
            padding = Insets(0.0)
            style = "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #fff8dc;"

            // Titel
            val titleLabel = Label("Warnungen & Hinweise").apply {
                font = Font.font("System", FontWeight.BOLD, 14.0)
            }
            children.add(titleLabel)

            // ListView für Warnungen
            val warningsList = FXCollections.observableArrayList<String>(
                // TODO: Hier deine Warnungen eintragen
                "Beispiel:",
                 "⚠ Mitarbeiter X hat noch offene Urlaubsanträge",
                 "⚠ Personalmangel in Abteilung Y",
                 "ℹ Erinnerung: Teammeeting morgen um 10:00",
                "Lost"
            )

            val warningsListView = ListView(warningsList).apply {
                prefHeight = Double.MAX_VALUE
                fixedCellSize = 28.0
            }

            children.add(warningsListView)
            VBox.setVgrow(warningsListView, Priority.ALWAYS)
        }

        val  empty = VBox()



        // Alle Bereiche zum Grid hinzufügen
        gridPane.add(topArea, 0, 0)      // Oben Links
        gridPane.add(empty, 0, 1)   // Unten Links



        // Grid zum StackPane hinzufügen
        children.add(gridPane)
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