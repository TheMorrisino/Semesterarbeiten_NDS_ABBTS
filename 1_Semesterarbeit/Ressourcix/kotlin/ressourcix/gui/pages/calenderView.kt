package ressourcix.gui.pages

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.StackPane
import ressourcix.domain.Employee
import ressourcix.gui.GuiBorderPane

object calenderView : StackPane() {

    private val employees = GuiBorderPane.graphical.employees

    private val tableView = TableView<Employee>()

    private val idColumn = TableColumn<Employee, UInt>("ID")
    private val abbrevColumn = TableColumn<Employee, String>("Abkürzung")

    init {
        // ID-Spalte (UInt!)
        idColumn.setCellValueFactory {
            ReadOnlyObjectWrapper(it.value.getId())
        }

        // Abkürzungs-Spalte
        abbrevColumn.setCellValueFactory {
            SimpleStringProperty(it.value.abbreviationSting())
        }

        // Reihenfolge der Spalten ist hier festgelegt
        tableView.columns.setAll(idColumn, abbrevColumn)

        // Daten in die Tabelle
        tableView.items.addAll(employees)

        // Tabelle anzeigen
        children.add(tableView)
    }
}
