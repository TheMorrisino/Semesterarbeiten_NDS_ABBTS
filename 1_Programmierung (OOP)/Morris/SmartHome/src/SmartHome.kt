/*
  Projekt:      SmartHome
  Firma:        ABB Technikerschule
  Autor:        Marco Bontognali

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

    building.setLed(0, true)
}