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
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.domain.Employee

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val BOX_WIDTH = 800.0
private const val BOX_HEIGHT = 400.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

object filteredEmployee {

    fun build(
        department: Department,
        education: Education,
        employees: List<Employee>,
        onClose: () -> Unit
    ): Node {

        val departmentField = createTfl("$department","")
        val educationField = createTfl("$education","")

        val table = TableView<Employee>().apply {
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
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

        val departmentCol = TableColumn<Employee, String>("Abteilung").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getDepartment()?.toString() ?: "") }
            prefWidth = 70.0
        }

        val birthdayCol = TableColumn<Employee, String>("Geburtsdatum").apply {
            setCellValueFactory { SimpleStringProperty(it.value.toString()) }
            prefWidth = 70.0
        }

        val educationCol = TableColumn<Employee, String>("Ausbildung").apply {
            setCellValueFactory { SimpleStringProperty(it.value.getEducation()?.toString() ?: "") }
            prefWidth = 70.0
        }

        table.columns.setAll(
            idCol, abbrevCol, surnameCol,
            nameCol, rolleCol, pensumCol,
            departmentCol,birthdayCol,educationCol)
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

    private fun createButton(text: String): Button =
        Button(text).apply {
            prefHeight = BTN_HEIGHT
            prefWidth = BTN_WIDTH
            textAlignment = TextAlignment.CENTER
            alignment = Pos.CENTER
            isFocusTraversable = false
        }

    private fun createDataBox(labelText: String, field: TextField): VBox =
        VBox(6.0).apply {
            children.addAll(Label(labelText), field)
        }

    private fun createTfl(text: String,prompt: String): TextField =
        TextField(text).apply {
            promptText = prompt
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
            isEditable = false
            setDisable(true)
        }
}
