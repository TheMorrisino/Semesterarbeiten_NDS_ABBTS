class Annual_plan (
    private val Jahr: Int,
    private val KW: Int = 52,
    private var AnzMitarbeiter: Int) {


    fun erstellen(){

    }

    fun loeschen(){
        //plan.clear()
        val Spalte = KW
        val Zeile = AnzMitarbeiter

        for (Spalte in 1 .. KW){
            for (Spalte in 1 .. AnzMitarbeiter ){

            }
        }
    }

    fun importierenCSV(
        pfad: String){

    }

    fun exportierenCSV(
        pfad: String,
        dateiname: String){

    }

}