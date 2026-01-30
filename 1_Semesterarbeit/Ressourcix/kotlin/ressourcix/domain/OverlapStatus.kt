package ressourcix.domain

enum class OverlapStatus {
    OK,       // Grün: Keine Überschneidung
    WARNING,  // Gelb: Überschneidung vorhanden, aber unter der Limite
    CRITICAL  // Rot: Maximales Limit erreicht

}