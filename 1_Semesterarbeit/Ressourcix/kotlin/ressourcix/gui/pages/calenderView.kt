package ressourcix.gui.pages

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import ressourcix.gui.GuiBorderPane

object calenderView : StackPane(Label("Kalender")) {

    val employees = GuiBorderPane.graphical.employees

    val abbreviationList = employees.map { it.abbreviationSting() }.toMutableList()
    val idList = employees.map { it.getId() }.toMutableList()
    val vacationEntry = employees.map { it.getVacationEntries() }.toMutableList()

    val tableView = TableView<String>()
    val column = TableColumn<String, String>("Abkürzung")

    init {
        column.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value)
        }

        tableView.columns.add(column)

        // Daten senkrecht in die Tabelle:
        tableView.items.addAll(abbreviationList)

        // TableView auch wirklich anzeigen:
        children.add(tableView)
    }
}
