import javafx.scene.chart.BubbleChart

fun main(){
    println("ok")
    var ganzezahl = 7                  // int
    var fliesskommazahl = 12.9          // Double
    var text1 = "Kotlin ist cool"       // String Zeichenkette
    var wahrFalsch = true               // Boolean
     wahrFalsch = false               // Boolean

    println("Zahl1: $ganzezahl")
    println("Zahl2: $fliesskommazahl")
    println("Text1: $text1")
    println("wahrFalsch: $wahrFalsch")


    println("Int min: ${Int.MIN_VALUE} max: ${Int.MAX_VALUE}")
    println("Double min: ${Double.MIN_VALUE} max: ${Double.MAX_VALUE}")
    println("UByte min: ${UByte.MIN_VALUE} max:${UByte.MAX_VALUE}")
    println("Byte min: ${Byte.MIN_VALUE} max:${Byte.MAX_VALUE}")
    println("Float min: ${Float.MIN_VALUE} max:${Float.MAX_VALUE}")

    var Buschtabe_C : Int
        Buschtabe_C = 67    //01000011
        Buschtabe_C.toByte()
        println(Buschtabe_C)


}