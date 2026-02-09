//  Autor:        Pedro Santos

package ressourcix.gui.pages

import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import ressourcix.app.app
import ressourcix.domain.Department
import ressourcix.domain.Education
import ressourcix.domain.Employee
import ressourcix.domain.Role
import ressourcix.gui.popUp.filterPopUp
import ressourcix.gui.popUp.filteredEmployeePopUp
import ressourcix.logger.logger
import ressourcix.gui.util.createButton
import ressourcix.gui.util.createDataBox
import ressourcix.gui.util.createLabeledComboBox
import ressourcix.gui.util.createTfl
import ressourcix.gui.util.lettersMaxFormatter
import ressourcix.gui.util.positiveIntNoZeroFormatter
import ressourcix.gui.util.promptWhenNull
import ressourcix.gui.components.PopupLayer
import ressourcix.gui.util.installBirthdayField
import ressourcix.gui.util.showEmployeeNotFound
import ressourcix.gui.util.showDeleteFailed
import ressourcix.gui.util.showSaveFailed

//TODO Executions abfangen, anzeigen und loggen

object employeeManagementView : BorderPane() {

    private val popupLayer = PopupLayer()

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
        promptWhenNull()
    }
    val departmentField = ComboBox<Department>().apply {
        items = FXCollections.observableArrayList(Department.values().toList())
        promptText = "Abteilung auswählen..."
        isFocusTraversable = false
        promptWhenNull()
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
        isFocusTraversable = false
        promptWhenNull()
    }
    var birthdayField = createTfl("","tt.mm.jjjj").apply {
        installBirthdayField(this)
        //TODO TextFormatter blockieren
    }

    var remainingVacationWeeksField = createTfl("","")
    var usedVacationWeeksField = createTfl("","")

    var newEmployee = false

    private val mainContent = BorderPane().apply {
        padding = Insets(10.0)
    }

    private val filterBar = HBox(50.0).apply {
        padding = Insets(20.0)
        alignment = Pos.CENTER

        val filterBtn = createButton("Filter").apply {
            setOnAction {
                popupLayer.show(
                    filterPopUp.build(
                        onClose = { popupLayer.close() },
                        onApply = { department, education ->
                            val filtered = app.employees.filter { emp ->
                                emp.getDepartment() == department && emp.getEducation() == education
                            }
                            logger.info("Mitarbeiter nach $department, $education. Gefunden: ${filtered.size} Mitarbeiter")
                            popupLayer.close()
                            popupLayer.show(
                                filteredEmployeePopUp.build(
                                    department = department,
                                    education = education,
                                    employees = filtered,
                                    onClose = { popupLayer.close() }
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
                    showEmployeeNotFound("Suche nach ID", idField.text)
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
                    showEmployeeNotFound("Suche nach Kürzel", kuerzel)
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
                createLabeledComboBox("Rolle", roleField),
                createLabeledComboBox("Abteilung", departmentField),
                createDataBox("Wohnort", cityField),
                createDataBox("Anzahl Ferienwochen", remainingVacationWeeksField)
            )
        }

        val rightColumm = VBox(20.0).apply {
            alignment = Pos.CENTER
            children.addAll(
                createDataBox("Nachname", surnameField),
                createDataBox("Pensum", workloadField),
                createLabeledComboBox("Ausbildung", educationField),
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
            createButton("Zurücksetzen").apply {
                setOnAction {resetSearch()}
            },
            createButton("MA einfügen").apply {
                setOnAction {
                    selectedEmployee = null
                    newEmployee = true
                    clearEmployeeFields()
                    setFieldsEditable(true)
                }

            },
            createButton("MA ändern").apply {
                setOnAction {
                    val emp = selectedEmployee
                    if (emp == null) {
                        showEmployeeNotFound("Mitarbeiter ändern", "Kein Mitarbeitender ausgewählt")
                        return@setOnAction
                    }
                    setFieldsEditable(true)
                }
            },
            createButton("MA speichern").apply {
                disableProperty().bind(
                    nameField.textProperty().isEmpty
                        .or(surnameField.textProperty().isEmpty)
                        .or(workloadField.textProperty().isEmpty)
                        .or(roleField.valueProperty().isNull)
                )
                    setOnAction {
                        try {
                            if (newEmployee) {
                                val emp = Employee(app.employeeIds.generateId())
                                generateEmployee(emp)
                                emp.abbreviationSting()
                                app.management.add(emp)
                                selectedEmployee = emp
                                newEmployee = false
                                setFieldsEditable(false)
                                fillEmployeeFields(emp)
                                calenderView.refreshVacations()
                                logger.info("Neuer Mitarbeitender unter ID:${emp.getId()} erfolgreich gespeichert.")
                                return@setOnAction
                            }
                            val emp = selectedEmployee
                            if (emp == null) {
                                showEmployeeNotFound("Mitarbeiter speichern", "Kein Mitarbeitender ausgewählt")
                                return@setOnAction
                            }

                            if (!isEditMode) return@setOnAction

                            generateEmployee(emp)
                            emp.abbreviationSting()
                            setFieldsEditable(false)
                            fillEmployeeFields(emp)
                            calenderView.refreshVacations()
                            logger.info("Mitarbeitender unter ID:${emp.getId()} erfolgreich gespeichert.")

                        } catch (e: Exception) {
                            logger.error("Speichern fehlgeschlagen: ${e.message}", e)
                            showSaveFailed(e.message)
                        }
                    }
            },
            createButton("MA löschen").apply {
                setOnAction {
                    val emp = selectedEmployee
                    if (emp == null) {
                        showEmployeeNotFound("Mitarbeiter löschen", "Kein Mitarbeitender ausgewählt")
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
                        showDeleteFailed(emp.getId())
                    }
                }
            }

        )
    }

    init {
        mainContent.top = filterBar
        mainContent.center = employeeInfoBox
        mainContent.right = functionsBox
        center = popupLayer.wrap(mainContent) { popupLayer.close() }
        setFieldsEditable(false)
        setNoSelectionState()
    }

    private fun fillEmployeeFields(emp:Employee) {
        idField.text = emp.getId().toString()
        nameField.text = emp.getFirstName()
        surnameField.text = emp.getLastName()
        workloadField.text = "${emp.getWorkloadPercent()}"
        roleField.value = emp.getRole()
        departmentField.value = emp.getDepartment()
        educationField.value = emp.getEducation()
        abbreviationField.text = emp.getAbbreviation().ifBlank { emp.abbreviationSting() }
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
        educationField.value = null
        cityField.clear()
        workloadField.clear()
        birthdayField.clear()
        remainingVacationWeeksField.clear()
        usedVacationWeeksField.clear()
    }

    private fun resetSearch() {
        selectedEmployee = null
        newEmployee = false
        clearEmployeeFields()
        setFieldsEditable(false)
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



