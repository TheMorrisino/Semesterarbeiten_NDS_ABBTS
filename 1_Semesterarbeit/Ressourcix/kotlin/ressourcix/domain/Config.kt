package ressourcix.domain

object config {
    var minEmployeeNumber = 0
    var minApprenticeNumber = 0
    var minManagerNumber = 0
    //var vacationBlock    = 0
    //var vacationSchoolBlock = BooleanArray(53) = [true,t]
    var vacation25Years = 5
    var vacationOver25Years = 5
    var vacationOver50Years = 5

    var vacationBlock   = booleanArrayOf(
        false, // Woche 0 (nicht benutzt?)
        true,  // Woche 1
        false, // Woche 0 (nicht benutzt?)
        true,  // Woche 1
        false, // Woche 2
        false, // Woche 3
        true,  // Woche 4
        false, // Woche 5
        false, // Woche 6
        true,  // Woche 7
        false, // Woche 8
        false, // Woche 9
        false, // Woche 10
        true,  // Woche 11
        false, // Woche 12
        false, // Woche 13
        false, // Woche 14
        false, // Woche 15
        true,  // Woche 16
        false, // Woche 17
        false, // Woche 18
        true,  // Woche 19
        false, // Woche 20
        false, // Woche 21
        false, // Woche 22
        false, // Woche 23
        true,  // Woche 24
        false, // Woche 25
        true,  // Woche 26
        false, // Woche 27
        false, // Woche 28
        false, // Woche 29
        true,  // Woche 30
        false, // Woche 31
        false, // Woche 32
        true,  // Woche 33
        false, // Woche 34
        false, // Woche 35
        false, // Woche 36
        true,  // Woche 37
        false, // Woche 38
        false, // Woche 39
        false, // Woche 40
        false, // Woche 41
        true,  // Woche 42
        false, // Woche 43
        false, // Woche 44
        false, // Woche 45
        true,  // Woche 46
        false, // Woche 47
        false, // Woche 48
        false, // Woche 49
        false, // Woche 50
    )

    var vacationSchoolBlock = booleanArrayOf(
        false, // Woche 0 (nicht benutzt?)
        true,  // Woche 1
        false, // Woche 2
        false, // Woche 3
        true,  // Woche 4
        false, // Woche 5
        false, // Woche 6
        true,  // Woche 7
        false, // Woche 8
        false, // Woche 9
        false, // Woche 10
        true,  // Woche 11
        false, // Woche 12
        false, // Woche 13
        false, // Woche 14
        false, // Woche 15
        true,  // Woche 16
        false, // Woche 17
        false, // Woche 18
        true,  // Woche 19
        false, // Woche 20
        false, // Woche 21
        false, // Woche 22
        false, // Woche 23
        true,  // Woche 24
        false, // Woche 25
        true,  // Woche 26
        false, // Woche 27
        false, // Woche 28
        false, // Woche 29
        true,  // Woche 30
        false, // Woche 31
        false, // Woche 32
        true,  // Woche 33
        false, // Woche 34
        false, // Woche 35
        false, // Woche 36
        true,  // Woche 37
        false, // Woche 38
        false, // Woche 39
        false, // Woche 40
        false, // Woche 41
        true,  // Woche 42
        false, // Woche 43
        false, // Woche 44
        false, // Woche 45
        true,  // Woche 46
        false, // Woche 47
        false, // Woche 48
        false, // Woche 49
        false, // Woche 50
        true,  // Woche 51
        true   // Woche 52
    )

}



