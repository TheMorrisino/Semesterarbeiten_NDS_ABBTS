//  Autor:        Pedro Santos

package ressourcix.gui.pages

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import ressourcix.app.app
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.domain.Role
import ressourcix.gui.popUp.filterPopUp
import ressourcix.gui.popUp.filteredEmployeePopUp
import ressourcix.logger.logger

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

//TODO Code organisieren. Evt. Funktionen usw. auslagern.
//TODO Neue Mitarbeiter einfügen fehlt!!!! TextField sind disabled...

object employeeManagementView : BorderPane() {

    private var selectedEmployee: ressourcix.domain.Employee? = null
    private var isEditMode: Boolean = false

    var idField = createTfl("", "ID eingeben...").apply {
        textFormatter = positiveIntNoZeroFormatter()
    }
    var abbreviationField = createTfl("", "Kürzel eingeben").apply {
        textFormatter = lettersOnlyMax4Formatter()
    }
    var nameField = createTfl("","")
    val roleField = ComboBox<Role>().apply {
        items = FXCollections.observableArrayList(Role.values().toList())
        promptText = "Rolle auswählen..."
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
    }
    val departmentField = ComboBox<Department>().apply {
        items = FXCollections.observableArrayList(Department.values().toList())
        promptText = "Abteilung auswählen..."
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
        isFocusTraversable = false
    }
    var cityField = createTfl("","")

    var surnameField = createTfl("","")
    var workloadField = createTfl("","")
    val educationField = ComboBox<Education>().apply {
        items = FXCollections.observableArrayList(Education.values().toList())
        promptText = "Ausbildung auswählen..."
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
        isFocusTraversable = false
    }
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
                            logger.info("Mitarbeiter nach $department, $education. Gefunden: ${filtered.size} Mitarbeiter")
                            closePopup()
                            showPopup(
                                filteredEmployeePopUp.build(
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

        val showByIdBtn = createButton("MA nach ID\nanzeigen").apply {
            disableProperty().bind(idField.textProperty().isEmpty)
            setOnAction {
                val idUInt = idField.text.toUInt()
                val emp = app.employees.firstOrNull { it.getId() == idUInt }
                if (emp != null) {
                    logger.info("Suche nach ID: ${emp.getId()} Mitarbeiter: (${emp.getFirstName()} ${emp.getLastName()}) gefunden.")
                    selectedEmployee = emp
                    fillEmployeeFields(emp)
                    setFieldsEditable(false)
                } else {

                    selectedEmployee = null
                    showNotFoundAlert("Suche nach ID", idField.text)
                    clearEmployeeFields()
                    setFieldsEditable(false)
                }
            }
        }
        val idBox = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("ID:").apply {
                    style = "-fx-font-weight: bold;"
                },
                idField,
                showByIdBtn
            )
        }
        val showByAbbreviationBtn = createButton("MA nach Kürzel\nanzeigen").apply {
            disableProperty().bind(abbreviationField.textProperty().isEmpty)
            setOnAction {
                val kuerzel = abbreviationField.text.trim().uppercase()

                val emp = app.employees.firstOrNull {
                    val abbr = it.Abbreviation.ifBlank { it.abbreviationSting() }
                    abbr == kuerzel
                }

                if (emp != null) {
                    logger.info("Suche nach Kürzel: ${emp.Abbreviation} Mitarbeiter: (${emp.getFirstName()} ${emp.getLastName()}) gefunden.")
                    selectedEmployee = emp
                    fillEmployeeFields(emp)
                    setFieldsEditable(false)
                } else {
                    logger.warn("Kein Mitarbeiter unter Kürzel: ${abbreviationField.text} gefunden.")
                    selectedEmployee = null
                    showNotFoundAlert("Suche nach Kürzel", kuerzel)
                    clearEmployeeFields()
                    setFieldsEditable(false)
                }
            }
        }

        val abbreviationBox = HBox(10.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                Label("Kürzel:").apply {
                    style = " -fx-font-weight: bold;"
                },
                abbreviationField,
                showByAbbreviationBtn
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
                createComboBox("Rolle", roleField),
                createComboBox("Abteilung", departmentField),
                createDataBox("Wohnort", cityField),
                createDataBox("Anzahl Ferienwochen", remainingVacationWeeksField)
            )
        }

        val rightColumm = VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Nachname", surnameField),
                createDataBox("Pensum", workloadField),
                createComboBox("Ausbildung", educationField),
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
                    val emp = selectedEmployee
                    if (emp == null) {
                        showNotFoundAlert("Mitarbeiter ändern", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }
                    setFieldsEditable(true)
                }
            },
            createButton("MA speichern").apply {
                setOnAction {
                    val emp = selectedEmployee
                    if (emp == null) {
                        showNotFoundAlert("Mitarbeiter speichern", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }

                    if (!isEditMode) return@setOnAction

                    try {
                        emp.setFirstName(nameField.text)
                        emp.setLastName(surnameField.text)
                        val w = workloadField.text.trim().toInt()
                        emp.setWorkloadPercent(w.toUByte())
                        emp.setRole(roleField.value!!)
                        emp.setDepartment(departmentField.value)
                        emp.setEducation(educationField.value)
                        emp.setCity(cityField.text)
                        emp.setBirthdayFromString(birthdayField.text)
                        logger.info("Mitarbeitender unter ID:${emp.getId()} erfolgreich gespeichert.")

                        setFieldsEditable(false)
                        fillEmployeeFields(emp)
                    } catch (e: Exception) {
                        logger.error("Speichern fehlgeschlagen für ID:${emp.getId()}: ${e.message}", e)
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "Speichern fehlgeschlagen"
                            headerText = "Bitte Eingaben prüfen"
                            contentText = e.message ?: "Unbekannter Fehler"
                            showAndWait()
                        }
                    }
                }
            },
            createButton("MA löschen").apply {
                setOnAction {
                    val emp = selectedEmployee
                    if (emp == null) {
                        showNotFoundAlert("Mitarbeiter löschen", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }

                    val ok = app.employees.removeIf { it.getId() == emp.getId() }
                    if (ok) {
                        clearEmployeeFields()
                        idField.clear()
                        abbreviationField.clear()
                        setNoSelectionState()
                        logger.info("Mitarbeitender unter ID:${emp.getId()} erfolgreich gelöscht.")
                    } else {
                        logger.warn("Löschen fehlgeschlagen für ID:${emp.getId()})")
                        Alert(Alert.AlertType.WARNING).apply {
                            title = "Löschen"
                            headerText = "Mitarbeitender nicht gefunden"
                            contentText = "ID: ${emp.getId()}"
                            showAndWait()
                        }
                    }
                }
            },
            createButton("Suche \nZurücksetzen").apply {
                setOnAction {resetSearch()}
            }
        )
    }

    init {
        mainContent.top = filterBar
        mainContent.center = employeeInfoBox
        mainContent.right = functionsBox
        center = centerStack

        setFieldsEditable(false)
        setNoSelectionState()
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
            style = " -fx-font-weight: bold;"
        }

    private fun createDataBox(labelText: String, field: TextField): VBox =
        VBox(6.0).apply {
            children.addAll(Label(labelText).apply {
                style = " -fx-font-weight: bold;"
            }, field)
        }

    private fun <T> createComboBox(labelText: String, field: ComboBox<T>): VBox =
        VBox(6.0).apply {
            children.addAll(
                Label(labelText).apply { style = "-fx-font-weight: bold;" },
                field
            )
        }

    private fun positiveIntNoZeroFormatter(): TextFormatter<String> {
        return TextFormatter { change ->
            val newText = change.controlNewText
            val ok = newText.isEmpty() || (newText.matches(Regex("[1-9][0-9]*"))
                    && newText.length <=4)
            if (ok) change else null
        }
    }

    private fun lettersOnlyMax4Formatter(): TextFormatter<String> {
        return TextFormatter { change ->
            val newText = change.controlNewText.uppercase()
            val ok = newText.isEmpty() || newText.matches(Regex("[A-ZÄÖÜ]{1,4}"))
            if (ok) {
                change.text = change.text.uppercase()
                change
            } else null
        }
    }

    private fun fillEmployeeFields(emp: ressourcix.domain.Employee) {
        idField.text = emp.getId().toString()
        nameField.text = emp.getFirstName()
        surnameField.text = emp.getLastName()
        workloadField.text = "${emp.getWorkloadPercent()}"
        roleField.value = emp.getRole()
        departmentField.value = emp.getDepartment()
        educationField.value = emp.getEducation()
        val abbr = emp.Abbreviation.ifBlank { emp.abbreviationSting() }
        abbreviationField.text = abbr
        cityField.text = emp.getCity()
        birthdayField.text = emp.getBirthdayAsString()
        remainingVacationWeeksField.clear()
        usedVacationWeeksField.clear()
    }

    private fun clearEmployeeFields() {
        idField.clear()
        abbreviationField.clear()
        nameField.clear()
        surnameField.clear()
        roleField.value = null
        departmentField.value = null
        cityField.clear()
        workloadField.clear()
        educationField.value = null
        birthdayField.clear()
        remainingVacationWeeksField.clear()
        usedVacationWeeksField.clear()
    }

    private fun showNotFoundAlert(title: String, searchValue: String) {
        Alert(Alert.AlertType.WARNING).apply {
            this.title = title
            headerText = "Kein Mitarbeitender gefunden"
            contentText = "Suchwert: $searchValue"
            showAndWait()
        }
    }

    private fun resetSearch() {
        selectedEmployee = null
        setFieldsEditable(false)
        clearEmployeeFields()
        idField.clear()
        abbreviationField.clear()
    }

    private fun setFieldsEditable(editable: Boolean) {
        nameField.isDisable = !editable
        surnameField.isDisable = !editable
        workloadField.isDisable = !editable
        roleField.isDisable = !editable
        departmentField.isDisable = !editable
        educationField.isDisable = !editable
        cityField.isDisable = !editable
        birthdayField.isDisable = !editable
        remainingVacationWeeksField.isDisable = true
        usedVacationWeeksField.isDisable = true
        isEditMode = editable
    }

    private fun setNoSelectionState() {
        selectedEmployee = null
        setFieldsEditable(false)
    }
    }
