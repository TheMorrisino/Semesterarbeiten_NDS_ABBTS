//  Autor:        Pedro Santos

package ressourcix.gui.pages

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.app.app
import ressourcix.gui.popUp.filterPopUp
import ressourcix.gui.popUp.filteredEmployee

object employeeManagementView : BorderPane() {

    private const val BTN_HEIGHT = 50.0
    private const val BTN_WIDTH = 140.0
    private const val TFL_HEIGHT = 30.0
    private const val TFL_WIDTH = 300.0

    var idField = createTfl("", "ID eingeben...")
    var abbreviationField = createTfl("", "Kürzel eingeben")

    var nameField = createTfl("","")
    var roleField = createTfl("","")
    var departmentField = createTfl("","")
    var cityField = createTfl("","")

    var surnameField = createTfl("","")
    var workloadField = createTfl("","")
    var educationField = createTfl("","")
    var birthdayField = createTfl("","")

    var remainingVacationWeeksField = createTfl("","")
    var usedVacationWeeksField = createTfl("","")

    private val dim = Region().apply {
        style = "-fx-background-color: rgba(0,0,0,0.35);"
        isVisible = false
        isMouseTransparent = false
        isManaged = true
        setOnMouseClicked { closePopup() }
    }

    private val popupHost = StackPane().apply {
        isVisible = false
        isMouseTransparent = false
        isManaged = true
        alignment = Pos.CENTER
        maxWidth = Double.MAX_VALUE
        maxHeight = Double.MAX_VALUE

    }

    private val mainContent = BorderPane().apply {
        padding = Insets(10.0)
    }

    private val centerStack = StackPane().apply {
        children.addAll(mainContent, dim, popupHost)
        StackPane.setAlignment(dim, Pos.CENTER)
        StackPane.setAlignment(popupHost, Pos.CENTER)
    }

    private val filterBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val filterBtn = createButton("Filter").apply {
            setOnAction {
                showPopup(
                    filterPopUp.build(
                        onClose = { closePopup() },
                        onApply = { department, education ->
                            val filtered = app.employees.filter { emp ->
                                emp.getDepartment() == department && emp.getEducation() == education
                            }
                            closePopup()
                            showPopup(
                                filteredEmployee.build(
                                    department = department,
                                    education = education,
                                    employees = filtered,
                                    onClose = { closePopup() }
                                )
                            )
                        }
                    )
                )
            }
        }

        val idBox = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("ID:"),
                idField,
                createButton("MA nach ID\nanzeigen").apply {
                    setOnAction {  } //TODO Funktion einfügen
                }
            )
        }

        val abbreviationBox = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("Kürzel:"),
                abbreviationField,
                createButton("MA nach Kürzel\nanzeigen").apply {
                    setOnAction {  } //TODO Funktion einfügen
                }
            )
        }
        children.addAll(filterBtn, idBox, abbreviationBox)
    }

    private val employeeInfoBox = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val leftColumm = VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Name", nameField),
                createDataBox("Rolle", roleField),
                createDataBox("Abteilung", departmentField),
                createDataBox("Wohnort", cityField),
                createDataBox("Anzahl Ferienwochen", remainingVacationWeeksField)
            )
        }

        val rightColumm = VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Nachname", surnameField),
                createDataBox("Pensum", workloadField),
                createDataBox("Ausbildung", educationField),
                createDataBox("Geburtstag", birthdayField),
                createDataBox("Gebrauchte Ferienwochen", usedVacationWeeksField)
            )
        }
        children.addAll(leftColumm, rightColumm)
    }

    private val functionsBox = VBox(30.0).apply {
        padding = Insets(30.0)
        alignment = Pos.CENTER_RIGHT

        children.addAll(
            createButton("MA ändern").apply {
                setOnAction {
                    //TODO Funktion Verknüpfen
                }
            },
            createButton("MA speichern").apply {
                setOnAction {
                    //TODO Funktion Verknüpfen
                }
            },
            createButton("MA löschen").apply {
                setOnAction {
                    //TODO Funktion Verknüpfen
                }
            }
        )
    }

    init {
        mainContent.top = filterBar
        mainContent.center = employeeInfoBox
        //mainContent.bottom = vacationsBar
        mainContent.right = functionsBox
        center = centerStack
    }

    fun showPopup(popupContent: Node) {
        popupHost.children.setAll(popupContent)
        dim.isVisible = true
        popupHost.isVisible = true
        dim.toFront()
        popupHost.toFront()
    }

    fun closePopup() {
        popupHost.children.clear()
        dim.isVisible = false
        popupHost.isVisible = false
    }

    private fun createTfl(text: String,prompt: String): TextField =
        TextField(text).apply {
            promptText = prompt
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
            children.addAll(Label(labelText), field)
        }
}
