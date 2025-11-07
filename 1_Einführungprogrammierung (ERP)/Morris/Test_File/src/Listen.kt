fun main(){
     // Position       0 1 2  3       4
    val liste = listOf(4,7,5,200,4,9,8,7,6,4)

    println(liste)
    println(liste.size)
    println(liste.get(1))
    println(liste[1])
    println(liste.sum())



    println("----------------")
    println()
    for (element in liste) {            // Hier wird die ganze Liste ausgegebn!
        print("$element, ")
    }
    println()
    println("----------------")

    for (index in 0.. liste.size -1){ // kann deb Bereich auswählen aus dem Index / Liste
        print("Zahl: ${liste[index]}  ")
    }
    println()
    println("----------------")

    val liste2 = mutableListOf(4.3, 6.8, 5.3)

    liste2.add(3.0)
    println(liste2)
    liste2.remove(6.8)
    println(liste2)
    liste2.removeAt(1)
    println(liste2)
    liste2.add(0, 9.9)
    println(liste2)



}

