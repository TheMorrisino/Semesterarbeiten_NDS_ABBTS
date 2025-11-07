import kotlin.math.*


fun main () {
    var themaVerstanden = false
    println("Lernen")

    if (themaVerstanden) {
        println("gehen zum näschten Thema")
        println("gehen zum näschten Thema")
        println("gehen zum näschten Thema")
        println("gehen zum näschten Thema")
    }
    else {
        println("Thema nochmals studieren")
        println("Thema nochmals studieren")
        println("Thema nochmals studieren")
        println("Thema nochmals studieren")
    }

    println("Pasue")

    val option = "load"


    when (option) {

        "S" -> {
            println("Mama MIA!!")
            println("Option Speichern")
            println("(Ist Wichtig!)")
        }

        "A" -> {
            println("AAAAAHHHH")
        }
        "L", "load" -> {
            println("Option Loden")
            }
        else -> {
            println("Unbekannt Option")
        }
    }

}