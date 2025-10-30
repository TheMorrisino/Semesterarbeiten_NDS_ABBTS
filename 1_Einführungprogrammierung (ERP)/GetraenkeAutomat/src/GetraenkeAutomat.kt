import kotlin.math.*

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




    while (true) {
        print("Wasserstand: $wassertand | ")
        println("Kredit: %.2f".format(kredit))
        println("------------------")
        println("Ihre_Wahl: ")
        option = readln().toInt()
        println("Ihre Wahl war $option")



                if ((option > 1) && (option < 5)) {
            if ((option == KAFFEE) && (kredit >= KAFFE_PREIS) && (wassertand > 0.0)) {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= 2.5
                kredit -= KAFFE_PREIS
            }
            else if ((option == KAFFEE) && (kredit < KAFFE_PREIS)){
                println("zu wenig Kredit!")
            }
            else if ((option == OVO) && (kredit >= OVO_PREIS) && (wassertand > 0.0)) {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= 2.5
                kredit -= OVO_PREIS
            }
            else if ((option == OVO) && (kredit < OVO_PREIS)) {
                println("zu wenig Kredit!")
            }
            else if ((option == TEE) && (kredit >= TEE_PREIS) && (wassertand > 0.0)) {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= 2.5
                kredit -= TEE_PREIS
            }
            else if ((option == TEE) && (kredit < TEE_PREIS)) {
                println("zu wenig Kredit!")
            }
             else
                 println("Ungültige Eingabe")
        }

        when (option) {
            MUENZEN_EINWERFEN -> {
                println("Kredit laden:")
                kredit = readln().toDouble()
            }

            RETOUR_GELD -> {
                println("--------------------------")
                println("Dein Retoure Geld: $kredit")
                println("--------------------------")
                kredit = 0.0
            }

            BEENDEN -> {
                println("Automat wird heruntergefahren")
                break
            }
        }




        if (wassertand <= 0.0){
            wassertand = 0.0
            println("--------------------------")
            println("Bitte Wasserstand aufüllen!")
            println("--------------------------")
        }


    }

}