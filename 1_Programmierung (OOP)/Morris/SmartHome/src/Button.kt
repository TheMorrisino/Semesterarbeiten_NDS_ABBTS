import ch.abbts.buildingclient.building
import java.lang.Thread.sleep

// KLassen werden grossgeschrieben Objekte werden kleingeschrieben

class Button( private var name: String, private var nr: Int) {
//    var name = ""
//    var nr :Int = 0

    fun getName() = name
    fun getNr () = nr

    fun setName(name: String) {
        if (name.length > 3){
            this.name = name
        } else {
            println("Name zu Kurz")
        }
    }

    override fun toString() = "$name $nr"

    fun setNr (nr: Int) {
        this.nr = nr
    }

    private fun isPressed()= building.isButtonPressed(nr)

    fun waitForPressed() {
        while (isPressed().not()) {
            sleep(100)
        }
        println("Knopf gedrückt")
    }

}