import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

// Rechen Operationen

fun main () {
    var seiteA = 3.0
    var seiteB = 4.0
    var rechteckumfang = (2 * (seiteA + seiteB))

    println("Umfang des Rechtecks: $rechteckumfang")

    var seitenVergleich = seiteA < seiteB

    println("Seite A kleiner als Seite B ? : $seitenVergleich")

    var radius = 5.0
    var kreisFlaeche = radius.pow(2.0) * PI
    println("Kreisfläche: $kreisFlaeche")
    println(round(kreisFlaeche))

    println(10.0 / 3.0)
    println(10 / 3)
    println(10 % 3)

    var zahl = 25.0
    println(sqrt(zahl))

    zahl = 27.0
    println(zahl.pow(1.0 /3.0))

    var habeDasProkduktNochNicht = true
    var braucheDasProdukt = false
    var preis = 50
    var sparkonto = 1000

    if(habeDasProkduktNochNicht && braucheDasProdukt && (preis < sparkonto)) {
        println("Ja ich Kaufe!")
    }
        else {
            println("Ich brauche das NICHT!")

        }
    var text1 = "Hallo"
    var text2 = "Hallo"
    println(text2 == text1)

    println("abcd\tabcd")
    println("abcde\tabcde")
//


}