package aposalo.com.currencycalculator.util

import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.RoundingMode
import java.text.DecimalFormat

const val TAG = "EXTENSIONS"

class Extensions  {

     companion object {

         fun String.getCalculation(): String {
             return try {
                 val expression = ExpressionBuilder(this).build()
                 val solution = expression.evaluate().toString().getSolution()
                 solution
             }
             catch (e: Exception) {
                 val msg = e.message.toString()
                 Log.e(TAG, "getCalculation: $msg",e)
                 "Err"
             }
         }

         fun String.getSolution(): String {
             var solutionString = this.toFloat().toTwoDecimals()

             if (solutionString.endsWith(".0"))
                 solutionString = solutionString.replace(".0", "")

             return solutionString.replace(",",".")
         }

         private fun Float.toTwoDecimals(): String {
             val df = DecimalFormat("#.##")
             df.roundingMode = RoundingMode.HALF_UP
             val roundOff = df.format(this)
             return roundOff.toString()
         }
     }
}