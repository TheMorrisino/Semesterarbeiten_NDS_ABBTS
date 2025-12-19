import kotlin.UInt

class Employeeold(
    val Id: UInt = generateId(),
    var Role : UInt,
    var workloadPercentage : UByte,
    var Tags: List<UByte> = List(30) { 0u },        //z.B Lehrling im 1 Jahr, HF Asbildung, Anlehre, Diplomiert
    var Holidays : UByte,
    var AbsentOnBusiness : Boolean,                      //nicht im Geschäfft
)

{


    companion object {
        // To DO Liste in einer CSV Datei abspeichern und diese Liste jeweils in saveIdInList importieren und Vergleichen lassen
        private var IDcount : UInt = 0u
        private var idliste = mutableListOf<UInt>(0u,)


        private fun generateId() : UInt{
            while (IDcount in idliste) {
                IDcount += 1u


            }
            val saveIdInList = (IDcount)
            idliste.add(saveIdInList)

            //DEBUG
            println()
            println("Debug")
            println("IDLISTE: $idliste")
            println("IDCOUNT: $IDcount")
            println("saveIdInList: $saveIdInList")

            return saveIdInList


        }


    }

}

