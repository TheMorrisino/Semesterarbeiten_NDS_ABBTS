// Autor Tiago

package ressourcix.domain

object config {
    var minEmployeeNumber = 0
    var minApprenticeNumber = 0
    var minManagerNumber = 0

    var vacation25Years = 5
    var vacationOver25Years = 5
    var vacationOver50Years = 5

    var vacationBlock = BooleanArray(53) { false }
    var vacationSchoolBlock = BooleanArray(53) { false }

}



