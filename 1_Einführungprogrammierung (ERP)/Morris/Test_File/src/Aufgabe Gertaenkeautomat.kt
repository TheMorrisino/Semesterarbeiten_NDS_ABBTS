fun main() {

    // Basis Funktionen
    val BEENDEN = 0
    val MUENZEN_EINWERFEN = 1
    val KAFFEE = 2
    val OVO = 3
    val TEE = 4
    val RETOUR_GELD = 5

    //Erweiterte  Funktionen
    var option: Int
    var wassertand: Double = 10.0
    var kredit: Double = 0.0

    // Preise der Items
    val KAFFE_PREIS = 1.00
    val OVO_PREIS = 1.10
    val TEE_PREIS = 0.80
    //

    println("------- MENUE -------")
    println("Muenze einwerfen....1")
    println("Kaffee ($KAFFE_PREIS CHF)...$KAFFEE")
    println("Ovo    ($OVO_PREIS CHF)...$OVO")
    println("Tee    ($TEE_PREIS CHF)...$TEE")
    println("Retour-Geld.........5")
    println("Beenden.............0")
    println("---------------------")
    println("Wasserstand: $wassertand")
    println("Kredit: $kredit")
    println("------------------")
    println("Wasserstand: $wassertand")
    println("Kredit: $kredit")
    println("Ihre_Wahl: ")
    option = readln().toInt()
    println("Ihre Wahl war $option")




}