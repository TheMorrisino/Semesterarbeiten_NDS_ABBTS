/*
  Projekt:      SmartHome
  Firma:        ABB Technikerschule
  Autor:        Morris Meier

  Beschreibung: Vorlage fuer die objektorientierte Gebaeudesteuerung mit realer Hardware.
                Für Building Dokumentation siehe README.md
*/

import ch.abbts.buildingclient.building

fun main() {
    println("SmartHome")
    //building.hardwareOn()            // Automatisches Suchen nach dem COM-Port
    building.hardwareOn("COM3") // Angabe des gewuenschten COM-Ports:
                                      // - Windows: COM3
                                      // - Mac:     tty.usbmodem3
                                      // - Linux:   /dev/tty3

//    building.setLed(0, true)

    var temp = building.getTemperature()
    println(temp)

//    println(buttonBlue.name)
//    println(buttonBlue.nr)
//    println("VOR")
//    buttonBlue.waitForPressed()
//    println("Nach")

    val buttonBlue = Button("Taste Blau", 0)

    val buttonRed  = Button("Taste Rot",1)

    println(buttonBlue.toString())
    println(buttonBlue)


    println(buttonBlue.getName())
    buttonBlue.waitForPressed()
    buttonBlue.setName("Bla")
    println(buttonBlue.getName())

    println(buttonRed.getName())
    buttonRed.waitForPressed()





}