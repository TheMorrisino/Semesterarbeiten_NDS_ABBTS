package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.text.TextAlignment
import ressourcix.domain.Department
import ressourcix.domain.Education

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val BOX_HEIGHT = 250.0
private const val BOX_WIDTH = 520.0

object filterPopUp {
    val departmentField = ComboBox<Department>().apply {
        items = FXCollections.observableArrayList(Department.values().toList())
        promptText = "Abteilung auswählen..."
        prefHeight = 30.0
        prefWidth = 300.0
    }
    val educationField = ComboBox<Education>().apply {
        items = FXCollections.observableArrayList(Education.values().toList())
        promptText = "Ausbildung auswählen..."
        prefHeight = 30.0
        prefWidth = 300.0
    }

    private fun resetFields() {
        departmentField.value = null
        educationField.value = null
        departmentField.requestLayout()
        educationField.requestLayout()
    }

    fun build(onClose: () -> Unit, onApply: (department: Department, education: Education) -> Unit): Node =
        VBox(12.0).apply {
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

            fun isValid(): Boolean = departmentField.value != null && educationField.value != null

            val applyBtn = createButton("Filter\neinsetzen").apply {
                disableProperty().bind(
                    Bindings.createBooleanBinding(
                        { !isValid() },
                        departmentField.valueProperty(),
                        educationField.valueProperty()
                    )
                )
                setOnAction {
                    val dep = departmentField.value ?: return@setOnAction
                    val edu = educationField.value ?: return@setOnAction

                    onApply(dep, edu)
                    resetFields()
                }
            }

            val cancelBtn = createButton("Abbrechen").apply {
                setOnAction {
                    resetFields()
                    onClose()
                }
            }

            children.addAll(
                Label("Mitarbeiter suchen"),
                VBox().apply {
                    children.addAll(
                        HBox().apply {
                            children.addAll(
                                createComboBox("Abteilung:", departmentField),
                                createComboBox("Ausbildung:", educationField)
                            )
                        },
                        HBox().apply {
                            alignment = Pos.CENTER
                            padding = Insets(20.0)
                            spacing = 100.0
                            children.addAll(applyBtn, cancelBtn)
                        }
                    )
                }
            )
        }
}
private fun createComboBox(labelText: String, choise: ComboBox<*>) = VBox(6.0).apply {
    padding = Insets(20.0)
    children.addAll(Label(labelText), choise)
    }

private fun createButton(text: String) = Button(text).apply {
    prefHeight = BTN_HEIGHT
    prefWidth = BTN_WIDTH
    textAlignment = TextAlignment.CENTER
    alignment = Pos.CENTER
    isFocusTraversable = false
    }



