package ressourcix.gui.popUp

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.domain.Employee

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val BOX_WIDTH = 900.0
private const val BOX_HEIGHT = 650.0

object filteredEmployee {
    fun build(
        department: Department,
        education: Education,
        employees: List<Employee>,
        onClose: () -> Unit
    ): Node {

        val table = TableView<Employee>().apply {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            prefHeight = 500.0
        }

        val idCol = TableColumn<Employee, UInt>("ID").apply {
            setCellValueFactory { ReadOnlyObjectWrapper(it.value.getId()) }
            prefWidth = 70.0
        }

        val abbrevCol = TableColumn<Employee, String>("Kürzel").apply {
            setCellValueFactory { SimpleStringProperty(it.value.abbreviationSting()) }
            prefWidth = 100.0
        }

        // Falls du Methoden für diese Felder hast, hier anpassen:
        val nameCol = TableColumn<Employee, String>("Name").apply {
            setCellValueFactory { SimpleStringProperty(it.value.toString()) } // TODO ersetzen
            prefWidth = 200.0
        }

        table.columns.setAll(idCol, abbrevCol, nameCol)
        table.items.setAll(employees)

        val filterInfo = HBox(20.0).apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(10.0)
            children.addAll(
                Label("Aktive Filter:"),
                Label("Abteilung: $department"),
                Label("Ausbildung: $education")
            )
        }

        val closeBtn = Button("Schliessen").apply {
            prefHeight = BTN_HEIGHT
            prefWidth = BTN_WIDTH
            textAlignment = TextAlignment.CENTER
            isFocusTraversable = false
            setOnAction { onClose() }
        }

        return VBox(12.0).apply {
            padding = Insets(20.0)
            maxWidth = BOX_WIDTH
            maxHeight = BOX_HEIGHT
            style = """
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #cccccc;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 4);
            """.trimIndent()

            children.addAll(
                Label("Gefilterte Mitarbeiter"),
                filterInfo,
                table,
                HBox().apply {
                    alignment = Pos.CENTER_RIGHT
                    children.add(closeBtn)
                }
            )
        }
    }
}
