package ressourcix.gui.pages

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.gui.pages.employeeManagementView.createDataBox

object employeeManagementView : BorderPane() {

    private val content = BorderPane()
    private const val BTN_HEIGHT = 50.0
    private const val BTN_WIDTH = 120.0
    private const val TFL_HEIGHT = 30.0
    private const val TFL_WIDTH = 300.0

    private val idField = createTfl("")
    private val initialsField = createTfl("")

    private val nameField = createTfl("")
    private val roleField = createTfl("")
    private val departmentField = createTfl("")
    private val cityField = createTfl("")

    private val surnameField = createTfl("")
    private val workloadField = createTfl("")
    private val educationField = createTfl("")
    private val birthdayField = createTfl("")

    private val remainingVacationWeeksTfl = createTfl("")
    private val usedVacationWeeksTfl =createTfl("")

    private val filter = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER
        val filterBtn = createButton("Filter")
        val idBox = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("ID:"),
                createTfl(""),
                createButton("MA nach ID\nanzeigen")
            )
        }

        val kürzelBox = HBox(10.0).apply {
            alignment = Pos.CENTER_LEFT
            children.addAll(
                Label("Kürzel:"),
                createTfl(""),
                createButton("MA nach kürzel\nanzeigen")
            )
        }
        children.addAll(filterBtn, idBox,kürzelBox)
    }

    private val employeeInfoBox = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER
        children.addAll(
        VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Name", nameField),
                createDataBox("Rolle:", roleField),
                createDataBox("Abteilung", departmentField),
                createDataBox("Wohnort:", cityField),
                createDataBox("Anzahl Ferienwoche:", remainingVacationWeeksTfl)
            )
        }, VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Nachname", surnameField),
                createDataBox("Pensum:", workloadField),
                createDataBox("Ausbildung:", educationField),
                createDataBox("Geburtstag:", birthdayField),
                createDataBox("Gebrauchte Ferien:", usedVacationWeeksTfl)
            )
        })
    }

    private val vacationsBox = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER
        children.addAll(
            createButton("Ferien eintragen").apply {
                alignment = Pos.CENTER
            }
        )
    }

    private val functionsBox = VBox(30.0).apply {
        padding = Insets(30.0)
        alignment = Pos.CENTER_RIGHT

        children.addAll(
            createButton("MA ändern").apply {
                setOnAction {
                    val name = nameField.text
                    val surname = surnameField.text
                }
            },
            createButton("MA speichern").apply {
                setOnAction {
                    val name = nameField.text
                    val surname = surnameField.text
                }
            },
            createButton("MA löschen").apply {
                setOnAction {
                    val name = nameField.text
                    val surname = surnameField.text
                }
            }
        )
    }

    init {
        padding = Insets(10.0)
        top = filter
        center = employeeInfoBox
        bottom = vacationsBox
        right = functionsBox
    }

    private fun createTfl(text: String): TextField =
        TextField(text).apply {
            prefHeight = TFL_HEIGHT
            prefWidth = TFL_WIDTH
            isFocusTraversable = false
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
            children.addAll(
                Label(labelText).apply {
                    alignment = Pos.TOP_LEFT
                },
                field)
        }
}
