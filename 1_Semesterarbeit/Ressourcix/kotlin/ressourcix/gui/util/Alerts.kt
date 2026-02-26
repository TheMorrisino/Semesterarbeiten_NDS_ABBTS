
package ressourcix.gui.util

import javafx.scene.control.Alert

fun showWarn(title: String, header: String, content: String? = null) {
    Alert(Alert.AlertType.WARNING).apply {
        this.title = title
        headerText = header
        contentText = content ?: ""
        showAndWait()
    }
}

fun showError(title: String, header: String, content: String? = null) {
    Alert(Alert.AlertType.ERROR).apply {
        this.title = title
        headerText = header
        contentText = content ?: "Unbekannter Fehler"
        showAndWait()
    }
}

fun showEmployeeNotFound(searchTitle: String, searchValue: String) {
    showWarn(
        title = searchTitle,
        header = "Kein Mitarbeitender gefunden",
        content = "Suchwert: $searchValue"
    )
}

fun showDeleteFailed(id: UInt) {
    showWarn(
        title = "Löschen",
        header = "Mitarbeitender nicht gefunden",
        content = "ID: $id"
    )
}

fun showSaveFailed(message: String?) {
    showError(
        title = "Speichern fehlgeschlagen",
        header = "Bitte Eingaben prüfen",
        content = message
    )
}
