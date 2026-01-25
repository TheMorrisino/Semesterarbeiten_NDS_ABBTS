package ressourcix.gui.navigation

import javafx.scene.Node
import javafx.scene.layout.BorderPane
import ressourcix.gui.pages.*

class NavigationsController(private val root: BorderPane) {

    private val cache = mutableMapOf<Route, Node>()

    fun navigate(to: Route) {
        val view = cache.getOrPut(to) { createView(to) }
        root.center = view
    }

    private fun createView(route: Route): Node = when (route) {
        Route.DASHBOARD -> dashboardView
        Route.CALENDER -> calenderView
        Route.EMPLOYEEMANAGEMENT -> employeeManagementView
        Route.PARAMETER -> parameterview
    }
}
