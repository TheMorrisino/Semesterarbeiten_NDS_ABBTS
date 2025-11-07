fun main () {


    for (counter in 1..5){
        println("Counter: $counter")

    }

    println("Finito")

    var zahl = (1..20).random()
    while (zahl != 5){
        println("Zahl: $zahl")
        zahl = (1..20).random()
    }
    println(zahl)
    var eingabe: Int= 0

    do {

        println("eingabe 1,2 oder 3")
        eingabe = readln().toInt()


// hallo  ich möchte das auch haben fehler machen ist nicht toll bitte hilf mir diesen packet auch runterladnelmhsdjfbhskdb

    }  while(eingabe !in (1..3))
    println("Eingabe war: $eingabe")



}