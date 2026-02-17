package ressourcix.gui.popUp

import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.gui.util.*

private const val BOX_HEIGHT = 270.0
private const val BOX_WIDTH = 520.0

object filterPopUp {

    fun build(onClose: () -> Unit, onApply: (department: Department, education: Education) -> Unit): Node {
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
            }
        }

        val cancelBtn = createButton("Abbrechen").apply {
            setOnAction {
                onClose()
            }
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
                Label("Mitarbeiter suchen").apply {
                    style = "-fx-font-size: 18px; -fx-font-weight: bold;"
                },
                VBox().apply {
                    children.addAll(
                        HBox(40.0).apply {
                            spacing = 40.0
                            padding = Insets(10.0, 0.0, 10.0, 0.0)
                            children.addAll(
                                createLabeledComboBox("Abteilung:", departmentField),
                                createLabeledComboBox("Ausbildung:", educationField)
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
}




