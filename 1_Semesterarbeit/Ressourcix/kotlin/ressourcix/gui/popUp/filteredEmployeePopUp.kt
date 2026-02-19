package ressourcix.gui.popUp

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.domain.Employee
import ressourcix.gui.util.*

private const val BOX_WIDTH = 800.0
private const val BOX_HEIGHT = 420.0

object filteredEmployeePopUp {

    fun build(
        department: Department,
        education: Education,
        employees: List<Employee>,
        onClose: () -> Unit
    ): Node {

        val departmentField = createTfl("$department","").apply {
            isDisable = true
        }
        val educationField = createTfl("$education","").apply {
            isDisable = true
        }

        val table = TableView<Employee>().apply {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
            maxWidth = 800.0
            maxHeight = 200.0
        }

        val idCol = TableColumn<Employee, UInt>("ID").apply {
            setCellValueFactory { ReadOnlyObjectWrapper(it.value.getId()) }
            prefWidth = 30.0
        }

        val abbrevCol = TableColumn<Employee, String>("Kürzel").apply {
            setCellValueFactory { SimpleStringProperty(it.value.abbreviationSting()) }
            prefWidth = 30.0
        }

        val surnameCol = TableColumn<Employee, String>("Name").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getLastName()) }
            prefWidth = 60.0
        }

        val nameCol = TableColumn<Employee, String>("Vorname").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getFirstName()) }
            prefWidth = 60.0
        }
        val rolleCol = TableColumn<Employee, String>("Rolle").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getRole().toString()) }
            prefWidth = 70.0
        }

        val pensumCol = TableColumn<Employee, String>("Pensum").apply {
            setCellValueFactory { SimpleStringProperty("${it.value.getWorkloadPercent()}%") }
            prefWidth = 50.0
        }

        val birthdayCol = TableColumn<Employee, String>("Geburtsdatum").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getBirthdayAsString()) }
            prefWidth = 70.0
        }

        val departmentCol = TableColumn<Employee, String>("Abteilung").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getDepartment()?.toString() ?: "") }
            prefWidth = 70.0
        }

        val educationCol = TableColumn<Employee, String>("Ausbildung").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getEducation()?.toString() ?: "") }
            prefWidth = 70.0
        }

        table.columns.setAll(
            idCol, abbrevCol, surnameCol,
            nameCol, rolleCol, pensumCol,
            birthdayCol,departmentCol,educationCol)
        table.items.setAll(employees)

        val filterInfo = HBox(20.0).apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(10.0)
            children.addAll(
                createDataBox("Abteilung:",departmentField),
                createDataBox("Ausbildung:",educationField)
            )
        }

        val closeBtn = createButton("Schliessen").apply {
            textAlignment = TextAlignment.CENTER
            isFocusTraversable = false
            setOnAction { onClose() }
        }

        return VBox(12.0).apply {
            padding = Insets(20.0)
            maxWidth = BOX_WIDTH
            maxHeight = BOX_HEIGHT
            VBox.setVgrow(table, Priority.ALWAYS)
            style = """
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #cccccc;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 4);
            """.trimIndent()

            children.addAll(
                Label("Gefilterte Mitarbeiter").apply {
                    style = "-fx-font-size: 18px; -fx-font-weight: bold;"
                },
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
