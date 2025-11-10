import kotlin.math.*

fun main() {



    // Basis Funktionen
    val BEENDEN = 0
    val MUENZEN_EINWERFEN = 1 // 1,2,5 CHF | 0.10, 0.20, 0.50, CHF
    val KAFFEE = 2
    val OVO = 3
    val TEE = 4
    val RETOUR_GELD = 5

    //Erweiterte  Funktionen
    var option: Int
    var wassertand: Double = 10.0
    var kredit: Double = 21.40

    // Preise der Items
    val KAFFE_PREIS = 1.00
    val OVO_PREIS = 1.10
    val TEE_PREIS = 0.80

    // MUENZEN-Liste
    val MUENZEN = mutableListOf(
        0.10,   // 10 Rappen
        0.20,   // 20 Rappen
        0.50,   // 50 Rappen
        1.00,   // 1.00 Franken
        2.00,   // 2.00 Franken
        5.00    // 5.00 Franken
    )






    while (true) {
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
        println("Ihre_Wahl: ")
        option = readln().toInt()
        println("------------------")
        println("Ihre Wahl war $option")
        println("------------------")


        val BECHER_VOlUMEN = 2.5

        when  {

            ((KAFFEE == option) && (kredit >= KAFFE_PREIS) && (wassertand >= BECHER_VOlUMEN)) -> {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= BECHER_VOlUMEN
                kredit -= KAFFE_PREIS
            }
            ((option == KAFFEE) && (kredit < KAFFE_PREIS)) -> {
                println("zu wenig Kredit!")
            }
            ((option == OVO) && (kredit >= OVO_PREIS) && (wassertand > 0.0)) -> {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= BECHER_VOlUMEN
                kredit -= OVO_PREIS
            }
            ((option == OVO) && (kredit < OVO_PREIS)) -> {
                println("zu wenig Kredit!")
            }
            ((option == TEE) && (kredit >= TEE_PREIS) && (wassertand > 0.0)) -> {
                println("Vielen Dank für Ihren Einkauf!")
                wassertand -= BECHER_VOlUMEN
                kredit -= TEE_PREIS
            }
            ((option == TEE) && (kredit < TEE_PREIS)) -> {
                println("zu wenig Kredit!")
            }
            option == MUENZEN_EINWERFEN -> {
                /*
                println("Bitte folgende Münzen einwerfen 0.1Rp, 0.2Rp, 0.5Rp, 1Fr, 2Fr, 5Fr,")

                var einwurf_muenze: Double
                einwurf_muenze = readln().toDouble()
                if (Prüfung_Muenze.Muenzen_Validieren(einwurf_muenze))  {
                    kredit += einwurf_muenze
                }
                else
                    println("Bitte eingabe wiederholen, Ungültige Münze!")
                */

                println("Bitte folgende Münzen einwerfen 0.1Rp, 0.2Rp, 0.5Rp, 1Fr, 2Fr, 5Fr,")
                var einwurf_muenze: Double
                var weitereMuenzeEinwerfen : Int = 0

                    do {
                        println("Bitte Münzen einwerfen")
                        einwurf_muenze = readln().toDouble()


                        if (einwurf_muenze in MUENZEN) {
                            kredit += einwurf_muenze
                        }

                        if (einwurf_muenze !in MUENZEN) {
                            println("Bitte eingabe wiederholen, Ungültige Münze!")
                        }

                        println("Weiter Münzen einwerfen ? Wenn ja gebe 1 ein wenn Nein gebe 0 ")
                        weitereMuenzeEinwerfen = readln().toInt()

                    } while ((einwurf_muenze !in MUENZEN) || (weitereMuenzeEinwerfen != 0))

                    println("Eingeworfene Münze: $einwurf_muenze Kredit: $kredit")
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
            }

            else  -> {
                println("Ungültige Eingabe!")
            }
        }
    }
}