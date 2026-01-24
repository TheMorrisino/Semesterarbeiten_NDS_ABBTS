package ressourcix.gui.navigation

import javafx.scene.control.Button
import javafx.scene.layout.HBox

object TopNavigationBar {

    private const val NAV_HEIGHT = 30.0

    private val dashboardBtn = createNavButton("Dashboard")
    private val calenderBtn = createNavButton("Kalender")
    private val employeeManagementBtn = createNavButton("Mitarbeiter Verwaltung")
    private val parameterBtn = createNavButton("Parameter")

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

            )
    }

    private fun createNavButton(text: String): Button {
        val btn = Button(text).apply {
            prefHeight = NAV_HEIGHT
            minHeight = NAV_HEIGHT
            maxHeight = NAV_HEIGHT
            isFocusTraversable = false
            style = baseButtonStyle()
        }

        btn.setOnMouseEntered {
            if (!btn.properties.containsKey("active")) {
                btn.style = hoverButtonStyle()
            }
        }

        btn.setOnMouseExited {
            if (!btn.properties.containsKey("active")) {
                btn.style = baseButtonStyle()
            }
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

        // Startzustand
        setActive(dashboardBtn)
    }

    fun getView(): HBox = navBar

    private fun baseButtonStyle(): String = """
        -fx-background-color: transparent;
        -fx-background-insets: 0;
        -fx-border-color: transparent;
        -fx-border-width: 0;
        -fx-focus-color: transparent;
        -fx-faint-focus-color: transparent;
        -fx-padding: 0 12 0 12;
        -fx-text-fill: black;
    """.trimIndent()

    private fun hoverButtonStyle(): String = """
        -fx-background-color: #dcdcdc;
        -fx-background-insets: 0;
        -fx-border-color: transparent;
        -fx-border-width: 0;
        -fx-focus-color: transparent;
        -fx-faint-focus-color: transparent;
        -fx-padding: 0 12 0 12;
        -fx-text-fill: black;
    """.trimIndent()

    private fun activeButtonStyle(): String = """
        -fx-background-color: black;
        -fx-background-insets: 0;
        -fx-border-color: transparent;
        -fx-border-width: 0;
        -fx-focus-color: transparent;
        -fx-faint-focus-color: transparent;
        -fx-padding: 0 12 0 12;
        -fx-text-fill: white;
    """.trimIndent()

    private fun setActive(activeBtn: Button) {
        val allButtons = listOf(dashboardBtn, calenderBtn, employeeManagementBtn, parameterBtn)

        // alle zurücksetzen
        allButtons.forEach { btn ->
            btn.properties.remove("active")
            btn.style = baseButtonStyle()
        }

        // aktiven setzen
        activeBtn.properties["active"] = true
        activeBtn.style = activeButtonStyle()
    }
}




