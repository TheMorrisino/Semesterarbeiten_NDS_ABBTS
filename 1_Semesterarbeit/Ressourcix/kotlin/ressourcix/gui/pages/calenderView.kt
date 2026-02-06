package ressourcix.gui.pages

import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import ressourcix.app.app
//import ressourcix.calendar.consoleCalendarOutput
import ressourcix.domain.Employee
import ressourcix.domain.VacationStatus
import ressourcix.domain.code
import ressourcix.gui.popUp.vacationPopUp
import ressourcix.logger.logger

object calenderView : StackPane() {

    // ---------------- Overlay (Popup) ----------------

    private val dim = Region().apply {
        style = "-fx-background-color: rgba(0,0,0,0.35);"
        isVisible = false
        isManaged = false
        isMouseTransparent = false
        setOnMouseClicked { closePopup() }
    }

    private val popupHost = StackPane().apply {
        isVisible = false
        isManaged = false
        isMouseTransparent = false
        alignment = Pos.CENTER
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE
    }

    // ---------------- Data / Tables ----------------

    private val employees = app.employees

    private val fixedTable = TableView<Employee>()
    private val weekTable = TableView<Employee>()

    // (Name bleibt, nicht genutzt)
    private val vScroll = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        isVisible = false
        isManaged = false
    }

    private val idColumn = TableColumn<Employee, UInt>("ID").apply {
        setCellValueFactory { ReadOnlyObjectWrapper(it.value.getId()) }
        prefWidth = 60.0
        isSortable = false
        isReorderable = false
    }

    private val nameColumn = TableColumn<Employee, String>("Abkürzung").apply {
        setCellValueFactory { SimpleStringProperty(it.value.getFullName()) }
        prefWidth = 120.0
        isSortable = false
        isReorderable = false
    }

    private var currentYear: UInt = 2026u
    private val weekCodeCache: MutableMap<UInt, Array<String>> = mutableMapOf()

    /**
     * Spacer unten links: gleiche Höhe wie horizontale Scrollbar rechts
     */
    private val spacer = Region().apply {
        minHeight = 0.0
        prefHeight = 0.0
        maxHeight = 0.0
    }

    // Guards (damit nichts doppelt installiert wird)
    private var scrollSyncInstalled = false
    private var wheelForwardInstalled = false
    private var selectionSyncInstalled = false

    init {
        // --- Tabellen Setup ---
        fixedTable.columns.setAll(idColumn, nameColumn)
        fixedTable.columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        fixedTable.isFocusTraversable = false

        weekTable.columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        weekTable.isFocusTraversable = false

        // gleiche Zeilenhöhe gegen Drift/Offset
        val rowHeight = 24.0
        fixedTable.fixedCellSize = rowHeight
        weekTable.fixedCellSize = rowHeight

        // Items teilen (gleiche Reihenfolge!)
        fixedTable.items.setAll(employees)
        weekTable.items = fixedTable.items

        // linke Tabelle: Breite fix
        val fixedWidth = idColumn.prefWidth + nameColumn.prefWidth + 24.0
        fixedTable.minWidth = fixedWidth
        fixedTable.prefWidth = fixedWidth
        fixedTable.maxWidth = fixedWidth

        // leftPane = fixedTable + spacer unten
        val leftPane = BorderPane().apply {
            center = fixedTable
            bottom = spacer
        }

        // center = leftPane direkt neben weekTable (kein Abstand)
        val center = HBox(leftPane, weekTable).apply {
            HBox.setHgrow(leftPane, Priority.NEVER)
            HBox.setHgrow(weekTable, Priority.ALWAYS)
        }

        val root = BorderPane().apply { this.center = center }

        // ✅ children NUR EINMAL füllen -> kein duplicate children
        children.setAll(root, dim, popupHost, vScroll)

        // RowFactory ohne updateItem-style-spam (weniger Flackern)
        fixedTable.setRowFactory { makeRow() }
        weekTable.setRowFactory { makeRow() }


        showYear(2026u)


        fixedTable.skinProperty().addListener { _, _, _ -> Platform.runLater { installOnceOrRefresh() } }
        weekTable.skinProperty().addListener { _, _, _ -> Platform.runLater { installOnceOrRefresh() } }
        Platform.runLater { installOnceOrRefresh() }
    }

    private fun makeRow(): TableRow<Employee> {
        val row = TableRow<Employee>()

        row.selectedProperty().addListener { _, _, selected ->
            row.style = if (selected) {
                """
                -fx-background-color: rgba(30, 144, 255, 0.25);
                -fx-border-color: #1e90ff;
                -fx-border-width: 0 0 0 4px;
                """.trimIndent()
            } else ""
        }

        row.setOnMouseClicked { e ->
            if (e.clickCount == 2 && !row.isEmpty) onEmployeeDoubleClick(row.item)
        }
        return row
    }


    private fun installOnceOrRefresh() {
        val leftV = findScrollBar(fixedTable, Orientation.VERTICAL)
        val rightV = findScrollBar(weekTable, Orientation.VERTICAL)
        if (leftV == null || rightV == null) return

        // Linke vertikale Scrollbar verstecken (nur rechts sichtbar)
        hideVerticalBar(leftV)

        // Linke horizontale Scrollbar verstecken (frozen)
        findScrollBar(fixedTable, Orientation.HORIZONTAL)?.let { hideHorizontalBar(it) }

        // Spacer unten links: Höhe = Höhe der horizontalen Scrollbar rechts
        val rightH = findScrollBar(weekTable, Orientation.HORIZONTAL)
        if (rightH != null && !spacer.prefHeightProperty().isBound) {
            spacer.prefHeightProperty().bind(rightH.heightProperty())
            spacer.minHeightProperty().bind(rightH.heightProperty())
            spacer.maxHeightProperty().bind(rightH.heightProperty())
        }

        // Scroll Sync: rechts steuert links (one-way) -> Listener nur einmal
        if (!scrollSyncInstalled) {
            scrollSyncInstalled = true
            rightV.valueProperty().addListener { _, _, v ->
                leftV.value = v.toDouble()
            }
        }
        // direkt angleichen
        leftV.value = rightV.value

        // Wheel Forward nur einmal
        if (!wheelForwardInstalled) {
            wheelForwardInstalled = true
            forwardWheelScrollToWeekTable()
        }

        // Selection Sync einmal
        if (!selectionSyncInstalled) {
            selectionSyncInstalled = true
            fixedTable.selectionModel.selectedIndexProperty().addListener { _, _, idx ->
                val i = idx.toInt()
                if (i >= 0 && i != weekTable.selectionModel.selectedIndex) weekTable.selectionModel.select(i)
            }
            weekTable.selectionModel.selectedIndexProperty().addListener { _, _, idx ->
                val i = idx.toInt()
                if (i >= 0 && i != fixedTable.selectionModel.selectedIndex) fixedTable.selectionModel.select(i)
            }
        }
    }

    private fun forwardWheelScrollToWeekTable() {
        fixedTable.addEventFilter(ScrollEvent.SCROLL) { e ->
            weekTable.fireEvent(e.copyFor(weekTable, weekTable))
            e.consume()
        }
    }

    private fun findScrollBar(table: TableView<*>, orientation: Orientation): ScrollBar? {
        return table.lookupAll(".scroll-bar")
            .filterIsInstance<ScrollBar>()
            .firstOrNull { it.orientation == orientation }
    }

    private fun hideVerticalBar(bar: ScrollBar) {
        if (bar.orientation != Orientation.VERTICAL) return
        bar.isVisible = false
        bar.isManaged = false
        bar.isDisable = true
        bar.prefWidth = 0.0
        bar.maxWidth = 0.0
    }

    private fun hideHorizontalBar(bar: ScrollBar) {
        if (bar.orientation != Orientation.HORIZONTAL) return
        bar.isVisible = false
        bar.isManaged = false
        bar.isDisable = true
        bar.prefHeight = 0.0
        bar.maxHeight = 0.0
    }

    // ----------------- Cache / Table Generation -----------------

    /** Cache für alle Mitarbeiter für ein Jahr: KW1..KW52 */
    private fun rebuildCache(year: UInt, weeks: UInt = 52u) {
        weekCodeCache.clear()

        for (employee in employees) {
            val codes = Array(weeks.toInt() + 1) { "." } // index 0 unbenutzt
            val entries = employee.getVacationEntries().filter { it.year == year }
            val seen = BooleanArray(weeks.toInt() + 1)

            for (entry in entries) {
                val start = entry.range.startWeek.coerceAtLeast(1u)
                val end = entry.range.endWeek.coerceAtMost(weeks)

                for (week in start..end) {
                    val wi = week.toInt()
                    if (seen[wi]) {
                        throw IllegalStateException("Overlap detected: empId=${employee.getId()} year=$year week=$week")
                    }
                    seen[wi] = true

                    val status: VacationStatus? = entry.getStatus(week)
                    codes[wi] = if (status != null) {
                        "ID${entry.id}.${status.code}"
                    } else {
                        "."
                    }
                }
            }
            weekCodeCache[employee.getId()] = codes
        }
    }

    fun showYear(year: UInt, weeks: UInt = 52u) {
        currentYear = year

        fixedTable.items.setAll(employees)
        weekTable.items = fixedTable.items

        rebuildCache(year, weeks)

        weekTable.columns.clear()

        for (week in 1u..weeks) {
            val weekIndex = week.toInt()
            val overlapIndex = weekIndex - 1
            val title = "KW" + week.toString().padStart(2, '0')

            val weekCol = TableColumn<Employee, String>(title).apply {
                prefWidth = 55.0
                isSortable = false
                isReorderable = false

                setCellValueFactory { cell ->
                    val empId = cell.value.getId()
                    val code = weekCodeCache[empId]?.get(weekIndex) ?: "."
                    SimpleStringProperty(code)
                }

                setCellFactory {
                    object : TableCell<Employee, String>() {
                        override fun updateItem(code: String?, empty: Boolean) {
                            super.updateItem(code, empty)
                            if (empty) {
                                text = null
                                style = ""
                                return
                            }

                            text = code ?: "."

                            val overlaps = app.management.getOverlapList().getOrElse(overlapIndex) { 0 }
                            val bg = colorForOverlap(overlaps)
                            style = "-fx-background-color: $bg;"
                        }
                    }
                }
            }

            weekTable.columns.add(weekCol)
        }

        fixedTable.refresh()
        weekTable.refresh()

        Platform.runLater { installOnceOrRefresh() }
    }

    fun refreshVacations() {
        rebuildCache(currentYear, 52u)
        fixedTable.refresh()
        weekTable.refresh()
        Platform.runLater { installOnceOrRefresh() }
    }

    fun updateEmployees() {
        fixedTable.items.setAll(employees)
        weekTable.items = fixedTable.items
        showYear(currentYear)
    }

    // ----------------- Popup Handling -----------------

    private fun onEmployeeDoubleClick(employee: Employee) {
        val empId = employee.getId()
        vacationPopUp.idField.text = empId.toString()
        vacationPopUp.nameField.text = employee.getFullName()

        showPopup(
            vacationPopUp.build(
                onClose = { closePopup() },
                onSave = { kw ->
                    app.management.addVacationSafe(employee, kw.startKW, kw.endKW)
                    logger.info("Ferieneintrag hinzugefügt Mitarbeiter $empId von ${kw.startKW} bis ${kw.endKW} ")
                    app.management.updateOverlapList()
                    refreshVacations()
                    closePopup()
                },
                onRemove = { kw ->
                    app.management.removeVacation(empId, kw.startKW, kw.endKW)
                    logger.info("Ferieneintrag entfernt Mitarbeiter $empId mit ${kw.startKW}")
                    app.management.updateOverlapList()
                    refreshVacations()
                    closePopup()
                }
            )
        )
    }
    fun showPopup(popupContent: Node) {
        popupHost.children.setAll(popupContent)
        dim.isVisible = true
        dim.isManaged = true
        popupHost.isVisible = true
        popupHost.isManaged = true
        dim.toFront()
        popupHost.toFront()
    }

    fun closePopup() {
        popupHost.children.clear()
        dim.isVisible = false
        dim.isManaged = false
        popupHost.isVisible = false
        popupHost.isManaged = false
    }

    // ----------------- Farben -----------------

    private fun clamp01(x: Double) = x.coerceIn(0.0, 1.0)
    private fun lerp(a: Int, b: Int, t: Double): Int = (a + (b - a) * t).toInt()
    private fun rgb(r: Int, g: Int, b: Int) = String.format("#%02X%02X%02X", r, g, b)

    private fun colorForOverlap(count: Int): String {
        val green = intArrayOf(210, 245, 210)
        val yellow = intArrayOf(255, 250, 200)
        val red = intArrayOf(255, 200, 200)

        return when {
            count <= 2 -> rgb(green[0], green[1], green[2])
            count <= 4 -> {
                val t = clamp01((count - 2) / 2.0)
                rgb(
                    lerp(green[0], yellow[0], t),
                    lerp(green[1], yellow[1], t),
                    lerp(green[2], yellow[2], t)
                )
            }
            else -> rgb(red[0], red[1], red[2])
        }
    }
}
