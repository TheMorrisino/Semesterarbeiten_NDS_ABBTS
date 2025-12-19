fun main (){
    // Pedro Test Bereich




















































    // Tiago Test Bereich











































    // Morris Test Bereich

    var employeeID = IDManager()
    var FerienID = IDManager()


    var MeierMorris = Employee(employeeID.generateId(),Role = 10u, workloadPercentage = 80.toUByte(),
        Holidays = 20.toUByte(), AbsentOnBusiness = false)

    var DeSousaSaTiago = Employee(employeeID.generateId(),Role = 30u, workloadPercentage = 9000.toUByte(),
        Holidays = 2.toUByte(), AbsentOnBusiness = false)

    var SantosPedro = Employee(employeeID.generateId(),Role = 30u, workloadPercentage = 9000.toUByte(),
        Holidays = 8.toUByte(), AbsentOnBusiness = false)


    MeierMorris
    var employeeMorris = FerienEintrag(MeierMorris.Id)
        employeeMorris.addHolidayWeek(HolidayWeek(5u, 8u))

    employeeMorris.addHolidayWeek(HolidayWeek(3u, 2u))
    employeeMorris.addHolidayWeek(HolidayWeek(1u, 2u))
    employeeMorris.addHolidayWeek(HolidayWeek(9u, 52u))
//    employeeMorris.holidayWeeks.get(2)



    employeeMorris.removeHolidayWeek(HolidayWeek(9u,48u))
    employeeMorris.addHolidayWeek(HolidayWeek(45u, 50u))




    println()
    println("MAIN Programm")
    println(employeeMorris)
    employeeMorris.changeHolidayWeek(HolidayWeek(45u,51u),HolidayWeek(46u,48u))
    println(employeeMorris)
//    println(employeeMorris.holidayWeeks)



        println(MeierMorris.Id)
        println(DeSousaSaTiago.Id)
        println(SantosPedro.Id)

    println(employeeID.Issued(2u))








}
























