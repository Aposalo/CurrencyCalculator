package aposalo.com.currencycalculator.util

import com.faendir.rhino_android.RhinoAndroidHelper
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

 class Extensions  {

     companion object {

         fun String.getCalculation(rhinoAndroidHelper: RhinoAndroidHelper): String {
             return try {
                 var newData = this

                 while (newData.startsWith("0") && newData.length > 1)
                     newData = newData.substring(1)

                 val context = rhinoAndroidHelper.enterContext()
                 context.optimizationLevel = -1
                 val scriptable: Scriptable = context.initStandardObjects()
                 val res = context.evaluateString(scriptable, newData, "Javascript", 1, null).toString()
                 res.toTwoDecimalsString()
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