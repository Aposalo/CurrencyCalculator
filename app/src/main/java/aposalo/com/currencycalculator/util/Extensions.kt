package aposalo.com.currencycalculator.util

import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder

const val TAG = "EXTENSIONS"

class Extensions  {

     companion object {

         fun String.getCalculation(): String {
             return try {
                 val expression = ExpressionBuilder(this).build()
                 expression.evaluate().toString()
             }
             catch (e: Exception) {
                 val msg = e.message.toString()
                 Log.e(TAG, "getCalculation: $msg",e)
                 "Err"
             }
         }
     }
}