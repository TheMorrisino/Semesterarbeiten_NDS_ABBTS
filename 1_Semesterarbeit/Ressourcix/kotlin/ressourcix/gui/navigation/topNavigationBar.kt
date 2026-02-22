//  Autor:        Pedro Santos

package ressourcix.gui.navigation

import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.text.Text
import ressourcix.app.app
import ressourcix.gui.pages.calenderView

object topNavigationBar {

    private const val NAV_HEIGHT = 30.0

    private val dashboardBtn = createNavButton("Dashboard")
    private val calenderBtn = createNavButton("Kalender")
    private val employeeManagementBtn = createNavButton("Mitarbeiter Verwaltung")
    private val parameterBtn = createNavButton("Parameter")
    private val year : UInt = app.management.year
    private val version = Text("Jahr: [$year]   V1.0   ").apply {
        setDisable(false)
        style = "-fx-fill: #b0b0b0;"

    }
    private val spacer = Region().apply {
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private val navBar = HBox().apply {
        prefHeight = NAV_HEIGHT
        minHeight = NAV_HEIGHT
        maxHeight = NAV_HEIGHT
        style = "-fx-background-color: #f0f0f0;"
        children.addAll(
            dashboardBtn,
            calenderBtn,
            employeeManagementBtn,
            parameterBtn,
            spacer,
            version
            )
    }

    private fun createNavButton(text: String): Button {
        val btn = Button(text).apply {
            prefHeight = NAV_HEIGHT
            minHeight = NAV_HEIGHT
            maxHeight = NAV_HEIGHT
            isFocusTraversable = false
            styleClass.add("nav-button")
        }

        return btn
    }

    fun bind(router: NavigationsController) {
        dashboardBtn.setOnAction {
            setActive(dashboardBtn)
            router.navigate(Route.DASHBOARD)
        }

        calenderBtn.setOnAction {
            setActive(calenderBtn)
            calenderView.refreshVacations()
            router.navigate(Route.CALENDER)
        }

        employeeManagementBtn.setOnAction {
            setActive(employeeManagementBtn)
            router.navigate(Route.EMPLOYEEMANAGEMENT)
        }

        parameterBtn.setOnAction {
            setActive(parameterBtn)
            router.navigate(Route.PARAMETER)
        }

        setActive(dashboardBtn)
    }

    fun getView(): HBox = navBar

    private fun setActive(activeBtn: Button) {
        val allButtons = listOf(dashboardBtn, calenderBtn, employeeManagementBtn, parameterBtn)

        allButtons.forEach {
            it.styleClass.remove("nav-button-active")
        }

        activeBtn.styleClass.add("nav-button-active")
    }
}




