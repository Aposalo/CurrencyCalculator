package aposalo.com.currencycalculator.util

import net.objecthunter.exp4j.ExpressionBuilder

class Extensions  {

     companion object {

         fun String.getCalculation(): String {
             return try {
                 val expression = ExpressionBuilder(this).build()
                 val result = expression.evaluate().toString()
                 result.toTwoDecimalsString()
             }
             catch (e: Exception) {
                 "Err"
             }
         }

         fun String.toTwoDecimalsString(): String {
             var solutionString = this.toFloat().toTwoDecimals()

             if (solutionString.endsWith(".0")) {
                 solutionString = solutionString.replace(".0", "")
             }
             return solutionString
         }

         private fun Float.toTwoDecimals(): String {
             val solution = String.format("%.2f", this).toFloat()
             return solution.toString()
         }
     }
}