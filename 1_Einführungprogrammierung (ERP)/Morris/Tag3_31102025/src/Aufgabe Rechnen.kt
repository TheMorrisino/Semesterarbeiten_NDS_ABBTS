import javafx.beans.binding.DoubleExpression
import kotlin.math.*

fun main() {
    var x = (((5 + 3) * 2) - 1) / 2
    println("a) $x")

    x = ((4 * 3) / 2) % 4
    println("b) $x")
     x = 3
     x++
     x++
     x += 4
     println("c) $x")

     var y = (4 < 1) or (5 == (2 + 3))
     println("d) $y")

     y = (4 != (3 * 3)) and (2 >= 7)
     println("e) $y")

     y = ((true xor true) or true).not()
     println("f) $y")

    var z = (sqrt(49.0) - 4).pow(3.0)
    println("g) $z")

     z = round(10.0 / 3.0)
     println("h) $z")

    println(log10(100.0))

    var winkelGrad = 20.0 // Sinus von winkelGrad
    println(sin(PI / 180.0 * winkelGrad)) // (2 PI = 360


    winkelGrad = 20.0
    var g1: Double
    var g2: Double
    var zahl4 = 4.0
    var resultat : Double
    g1 = (log10(1000.0) * sin( PI / 180.0 * winkelGrad))
    g2 = (zahl4.pow(3.0) - (5))
    resultat = sqrt( g1) + g2


    println("Resulat: $resultat")
    println("Resulat: ${round(resultat)}")



}