import kotlin.math.*

// Menü Funktionen (option)
val BEENDEN = 0
val MUENZEN_EINWERFEN = 1

// MUENZEN-Liste
val MUENZEN = mutableListOf(
    0.10,   // 10 Rappen
    0.20,   // 20 Rappen
    0.50,   // 50 Rappen
    1.00,   // 1.00 Franken
    2.00,   // 2.00 Franken
    5.00    // 5.00 Franken
)
val KAFFEE = 2
val OVO = 3
val TEE = 4
val RETOUR_GELD = 5

//Kredit und Wasserstand
var wassertand: Double = 20.0
var kredit: Double = 5.00

// Preise der Items
val KAFFE_PREIS = 1.00
val OVO_PREIS = 1.10
val TEE_PREIS = 0.80

val BECHER_VOlUMEN = 2.5

fun main() {

    while (true) {

        ausgabeMenue()

        println("Bitte geben sie Ihre Wahl an.")
        var option: Int = readln().toInt()
        benutzereingabe(option)



        when  {

            ((KAFFEE == option) && (kredit >= KAFFE_PREIS) && (wassertand >= BECHER_VOlUMEN)) -> {
                macheKAffe()
            }
            ((KAFFEE == option) && (kredit < KAFFE_PREIS)) -> {
                println("zu wenig Kredit!")
            }
            ((option == OVO) && (kredit >= OVO_PREIS) && (wassertand > 0.0)) -> {
                machOVO()
            }
            ((option == OVO) && (kredit < OVO_PREIS)) -> {
                println("zu wenig Kredit!")
            }
            ((option == TEE) && (kredit >= TEE_PREIS) && (wassertand > 0.0)) -> {
                macheTee()
            }
            ((option == TEE) && (kredit < TEE_PREIS)) -> {
                println("zu wenig Kredit!")
            }

            option == MUENZEN_EINWERFEN -> {
//                Validierung der Münzen Option 1

//                var einwurfmuenze: Double
//                einwurfmuenze = readln().toDouble()
//                validierungMuenzeOP1(einwurfmuenze)
//
//                }
//                Validierung der Münzen Option 2


                validierungMuenzeOP2()
            }

            (option == RETOUR_GELD) && ( kredit > 0.0) -> {
                //println("Dein Retoure Geld:  %.2f".format(kredit))
                // Aufgabe a
//                var anzahl10rappen : Int = 0
//                while (kredit > MUENZEN[0]) {
//                    if (kredit >= MUENZEN[0]) {
//                        kredit -= MUENZEN[0]
//
//                        anzahl10rappen++
//                    }
//                }
//                println("Dein Retoure Geld:  %.2f".format(kredit ))
//                println("Anzahl 10 Räppler: $anzahl10rappen")

                // Aufgabe b = 0
               var rueckbetrag : Double = (kredit * 100 ).roundToInt() / 100.0
               var anzahl10Rp : Int = 0
               var anzahl20Rp : Int = 0
               var anzahl50Rp : Int = 0
               var anzahl1CHF : Int = 0
               var anzahl2CHF : Int = 0
               var anzahl5CHF : Int = 0



                while (kredit > MUENZEN[0]) {
                    if (kredit >= MUENZEN[5]) {
                        kredit -= MUENZEN[5]
                        anzahl5CHF++
                    }
                    else if (kredit >= MUENZEN[4]) {
                        kredit -= MUENZEN[4]
                        anzahl2CHF++
                    }
                    else if (kredit >= MUENZEN[3]) {
                        kredit -= MUENZEN[3]
                        anzahl1CHF++
                    }
                    else if (kredit >= MUENZEN[2]) {
                        kredit -= MUENZEN[2]
                        anzahl50Rp++
                    }
                    else if (kredit >= MUENZEN[1]) {
                        kredit -= MUENZEN[1]
                        anzahl20Rp++
                    }
                    else if (kredit >= MUENZEN[0]) {
                        kredit -= MUENZEN[0]
                        anzahl10Rp++
                    }

                    else {
                        println("Fehler")
                        break

                    }

                    kredit = (kredit * 100).roundToInt() / 100.0

                }

                println("Rückgeld von " + rueckbetrag + "0  vollständig ausgezahlt.")
                println("Verwendete Münzen:")
                if (anzahl5CHF > 0)   println("5.00CHF  × $anzahl5CHF")
                if (anzahl2CHF > 0)   println("2.00CHF  × $anzahl2CHF")
                if (anzahl1CHF > 0)   println("1.00CHF  × $anzahl1CHF")
                if (anzahl50Rp > 0)   println("0.50CHF  × $anzahl50Rp")
                if (anzahl20Rp > 0)   println("0.20CHF  × $anzahl20Rp")
                if (anzahl10Rp > 0)   println("0.10CHF  × $anzahl10Rp")

                // Falls ein Restbetrag übrig bleibt
                if (kredit > 0.0) {
                    println("Restbetrag, der nicht ausbezahlt werden konnte: %.2f CHF".format(kredit))
                }
            }


            option == BEENDEN -> {
                println("Automat wird heruntergefahren")
                println("BYe Bye!")
                break
            }
            wassertand <= 0.0 ->{
                wassertand = 0.0
                println("--------------------------")
                println("Bitte Wasserstand aufüllen!")
                println("--------------------------")
                println("Produkt konnte nicht zubereiten werden")
            }

            else  -> {
                println("Ungültige Eingabe!")
            }
        }
    }
}

fun ausgabeMenue (){
    println("------- MENUE -------")
    println("Muenze einwerfen....$MUENZEN_EINWERFEN")
    println("Kaffee (" + KAFFE_PREIS + "0 CHF)...$KAFFEE")
    println("Ovo    (%.2f".format(OVO_PREIS)+  " CHF)...$OVO")
    println("Tee    (%.2f".format(TEE_PREIS) + " CHF)...$TEE")
    println("Retour-Geld.........$RETOUR_GELD")
    println("Beenden.............$BEENDEN")
    println("---------------------")
    print("Wasserstand: $wassertand | ")
    println("Kredit: %.2f".format(kredit))
    println("------------------")
}

fun benutzereingabe (option : Int) {
    println("------------------")
    println("Ihre Wahl war $option")
    println("------------------")
}

fun  macheKAffe() {
        println("Ihr Kaffee wird zubereiten")
        println("Vielen Dank für Ihren Einkauf!")
        wassertand -= BECHER_VOlUMEN
        kredit -= KAFFE_PREIS
}

fun machOVO() {
    println("Ihr Ovo wird zubereiten")
    println("Vielen Dank für Ihren Einkauf!")
    wassertand -= BECHER_VOlUMEN
    kredit -= OVO_PREIS
}

fun macheTee() {
    println("Ihr Tee wird zubereiten")
    println("Vielen Dank für Ihren Einkauf!")
    wassertand -= BECHER_VOlUMEN
    kredit -= TEE_PREIS
}

fun validierungMuenzeOP1 (einwurfmuenze : Double) {
    if (Prüfung_Muenze.Muenzen_Validieren(einwurfmuenze))  {
        kredit += einwurfmuenze
        println("Münze Validiert und dem Kredit hinzugefügt!")

    }
    else
        println("Bitte eingabe wiederholen, Ungültige Münze!")

}

fun validierungMuenzeOP2 () {
    var weitereMuenzeEinwerfen: String
    var einwurf_muenze: Double

    do {
        println("Bitte folgende Münzen einwerfen 0.1Rp, 0.2Rp, 0.5Rp, 1Fr, 2Fr, 5Fr,")
        einwurf_muenze = readln().toDouble()
        println("Eingeworfen: $einwurf_muenze")
        if (einwurf_muenze in MUENZEN ) {
            kredit += einwurf_muenze
            println("Aktueller Kredit: $kredit")
            println("Weiter Münzen einwerfen ? (Ja/Nein) ")
            weitereMuenzeEinwerfen = readln()
            if (weitereMuenzeEinwerfen.equals("ja", ignoreCase = true)) {
                continue
            }
            if (weitereMuenzeEinwerfen.equals("nein", ignoreCase = true)) {
                break
            }else
                println("Falscher Input")
                println("Bitte ja oder nein eingeben")
                continue
        }
        if (einwurf_muenze !in MUENZEN) {
            println("Bitte eingabe wiederholen, Ungültige Münze!")
        }

    } while (true)

    println("Eingeworfener Kredit: $kredit")
}


fun rueckgeldIn10Rp (){

}