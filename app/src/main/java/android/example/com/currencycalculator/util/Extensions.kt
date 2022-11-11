package android.example.com.currencycalculator.util

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

 class Extensions  {

     companion object {

         fun String.getCalculation(): String {
             return try {
                 var newData = this

                 while (newData.startsWith("0") && newData.length > 1)
                     newData = newData.substring(1)

                 val context = Context.enter()
                 context.optimizationLevel = -1
                 val scriptable: Scriptable = context.initStandardObjects()
                 val res = context.evaluateString(scriptable, newData, "Javascript", 1, null).toString()
                 res.toTwoDecimals()
             }
             catch (e: Exception) {
                 "Err"
             }
         }

         fun String.toTwoDecimals(): String {
             val number = this.toFloat()
             val solution = String.format("%.2f", number).toFloat()

             var solutionString = solution.toString()

             if (solutionString.endsWith(".0")) {
                 solutionString = solutionString.replace(".0", "")
             }
             return solutionString
         }
     }
}