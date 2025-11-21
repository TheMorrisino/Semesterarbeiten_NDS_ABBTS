data class Employee(
    var Id : UInt,
    var Role : UInt,
    var workloadPercentage : UByte,
    var Tags: List<UByte> = List(30) { 0u },        //z.B Lehling im 1 Jahr, HF Asbildung, Anlehre, Diplomiert
    var Holidays : UByte,
    var AbsentOnBusiness : Boolean,                      //nicht im Geschäfft
    var Pedro : String

)