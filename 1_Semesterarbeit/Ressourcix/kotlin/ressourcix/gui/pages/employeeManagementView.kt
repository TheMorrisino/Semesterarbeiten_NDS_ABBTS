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
import ressourcix.domain.Employee
import ressourcix.domain.Role
import ressourcix.gui.popUp.filterPopUp
import ressourcix.gui.popUp.filteredEmployeePopUp
import ressourcix.logger.logger
import java.time.format.DateTimeFormatter

private const val BTN_HEIGHT = 50.0
private const val BTN_WIDTH = 140.0
private const val TFL_HEIGHT = 30.0
private const val TFL_WIDTH = 300.0

//TODO Code organisieren. Evt. Funktionen usw. auslagern.

object employeeManagementView : BorderPane() {

    private var selectedEmployee: Employee? = null
    private var isEditMode: Boolean = false

    var idField = createTfl("", "ID eingeben...").apply {
        textFormatter = positiveIntNoZeroFormatter(4,1000)
    }
    var abbreviationField = createTfl("", "Kürzel eingeben").apply {
        textFormatter = lettersMaxFormatter(4,true)
    }
    var nameField = createTfl("","").apply {
        textFormatter =lettersMaxFormatter(30,false)
    }
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
    var cityField = createTfl("","").apply {
        textFormatter = lettersMaxFormatter(30,false)
    }

    var surnameField = createTfl("","").apply {
        textFormatter = lettersMaxFormatter(30,false)
    }
    var workloadField = createTfl("","").apply {
        textFormatter = positiveIntNoZeroFormatter(3,100)
    }
    val educationField = ComboBox<Education>().apply {
        items = FXCollections.observableArrayList(Education.values().toList())
        promptText = "Ausbildung auswählen..."
        prefHeight = TFL_HEIGHT
        prefWidth = TFL_WIDTH
        isFocusTraversable = false
    }
    var birthdayField = createTfl("","tt.mm.jjjj").apply {
        textFormatter = birthdayTextFormatter()
        //TODO TextFormatter blockieren
    }

    var remainingVacationWeeksField = createTfl("","")
    var usedVacationWeeksField = createTfl("","")

    var newEmployee = false

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
                    val abbr = it.getAbbreviation().ifBlank { it.abbreviationSting() }
                    abbr == kuerzel
                }

                if (emp != null) {
                    logger.info("Suche nach Kürzel: ${emp.getAbbreviation()} Mitarbeiter: (${emp.getFirstName()} ${emp.getLastName()}) gefunden.")
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
            createButton("Suche \nZurücksetzen").apply {
                setOnAction {resetSearch()}
            },
            createButton("MA einfügen").apply {
                setOnAction {
                    setFieldsEditable(true)
                    clearEmployeeFields()
                    idField.isDisable = true
                    abbreviationField.isDisable = true
                    newEmployee = true
                }

            },
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
                    if (newEmployee){
                        val emp = generateEmployee(Employee(app.employeeIds.generateId()))
                        if (emp == null) {
                            showNotFoundAlert("Mitarbeiter speichern", "Kein Mitarbeitender ausgewählt")
                            return@setOnAction
                        }
                        app.management.add(emp)
                        fillEmployeeFields(emp)
                        setFieldsEditable(false)
                    }
                    else{


                    val emp = selectedEmployee
                    if (emp == null) {
                        showNotFoundAlert("Mitarbeiter speichern", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }

                    if (!isEditMode) return@setOnAction

                    try {
                        generateEmployee(emp)
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
                }
            },
            createButton("MA löschen").apply {
                setOnAction {
                    val emp = selectedEmployee
                    if (emp == null) {
                        showNotFoundAlert("Mitarbeiter löschen", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }

                    val ok = app.management.removeById(emp.getId())
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

    private fun positiveIntNoZeroFormatter(maxDigits: Int,maxValue: Int): TextFormatter<String> {
        return TextFormatter { change ->
            val newText = change.controlNewText
            val ok = newText.isEmpty() || (newText.matches(Regex("[1-9][0-9]*"))
                    && newText.length <= maxDigits
                    && newText.toInt() <= maxValue)
            if (ok) change else null
        }
    }

    private fun lettersMaxFormatter(maxLetters: Int, bigLetters: Boolean): TextFormatter<String> {
        val allowedChars = if (bigLetters) "A-ZÄÖÜ \\-" else "A-Za-zÄÖÜäöü  \\-"
        val pattern = Regex("^[$allowedChars]{0,$maxLetters}$")

        return TextFormatter { change ->
            if (!change.isContentChange) return@TextFormatter change
            if (bigLetters && change.text.isNotEmpty()) {
                change.text = change.text.uppercase()
            }
            val newText = change.controlNewText
            if (pattern.matches(newText)) change else null
        }
    }

    private fun fillEmployeeFields(emp:Employee) {
        idField.text = emp.getId().toString()
        nameField.text = emp.getFirstName()
        surnameField.text = emp.getLastName()
        workloadField.text = "${emp.getWorkloadPercent()}"
        roleField.value = emp.getRole()
        departmentField.value = emp.getDepartment()
        educationField.value = emp.getEducation()
        val abbr = emp.getAbbreviation().ifBlank { emp.abbreviationSting() }
        abbreviationField.text = abbr
        cityField.text = emp.getCity()
        birthdayField.text = emp.getBirthdayAsString()
        remainingVacationWeeksField.clear()
        usedVacationWeeksField.clear()
    }

    private fun generateEmployee(emp: Employee): Employee{
        emp.setFirstName(nameField.text)
        emp.setLastName(surnameField.text)
        val w = workloadField.text.trim().toInt()
        emp.setWorkloadPercent(w.toUByte())
        emp.setRole(roleField.value!!)
        emp.setDepartment(departmentField.value)
        emp.setEducation(educationField.value)
        emp.setCity(cityField.text)
        emp.setBirthdayFromString(birthdayField.text)
        return emp
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
        idField.isDisable = editable
        abbreviationField.isDisable = editable
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

private fun birthdayTextFormatter(): TextFormatter<String> {
    return TextFormatter { change ->

        if (!change.isContentChange) return@TextFormatter change

        val raw = change.controlNewText
        if (raw.isEmpty()) return@TextFormatter change

        if (!raw.all { it.isDigit() || it == '.' }) return@TextFormatter null

        val digits = raw.filter { it.isDigit() }
        if (digits.length > 8) return@TextFormatter null

        val formatted = buildString {
            digits.forEachIndexed { i, c ->
                append(c)
                if (i == 1 || i == 3) append('.')
            }
        }

        if (formatted.length > 10) return@TextFormatter null

        val oldLength = change.controlText.length
        val newLength = formatted.length

        change.text = formatted
        change.setRange(0, oldLength)

        val newCaretPos = when {
            newLength == 3 || newLength == 6 -> newLength
            newLength > oldLength -> change.caretPosition + 1
            else -> change.caretPosition
        }

        change.selectRange(newCaretPos, newCaretPos)
        change
    }
}

