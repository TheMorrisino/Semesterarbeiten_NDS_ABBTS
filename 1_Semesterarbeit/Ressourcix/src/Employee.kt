import kotlin.UInt

class Employee(
    val Id: UInt = generateId(),
    var Role : UInt,
    var workloadPercentage : UByte,
    var Tags: List<UByte> = List(30) { 0u },        //z.B Lehrling im 1 Jahr, HF Asbildung, Anlehre, Diplomiert
    var Holidays : UByte,
    var AbsentOnBusiness : Boolean,                      //nicht im Geschäfft
)

{


    companion object {
        private var IDcount : UInt = 0u
        private var idliste = mutableListOf<UInt>()


        private fun generateId() : UInt{
            while (IDcount in idliste) {
                IDcount += 1u


            }
            IDcount += 1u
            val saveidinlist = (IDcount -1u)
            idliste.add(saveidinlist)

            println(idliste) //DEBUG

            return saveidinlist


        }
    }
}

