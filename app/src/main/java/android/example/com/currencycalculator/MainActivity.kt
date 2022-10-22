package android.example.com.currencycalculator

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var resultTv: TextView? = null
    var solutionTv: TextView? = null
    var buttonC: MaterialButton? = null
    var buttonBrackOpen: MaterialButton? = null
    var buttonBrackClose: MaterialButton? = null
    var buttonDivide: MaterialButton? = null
    var buttonMultiply: MaterialButton? = null
    var buttonPlus: MaterialButton? = null
    var buttonMinus: MaterialButton? = null
    var buttonEquals: MaterialButton? = null
    var button0: MaterialButton? = null
    var button1: MaterialButton? = null
    var button2: MaterialButton? = null
    var button3: MaterialButton? = null
    var button4: MaterialButton? = null
    var button5: MaterialButton? = null
    var button6: MaterialButton? = null
    var button7: MaterialButton? = null
    var button8: MaterialButton? = null
    var button9: MaterialButton? = null
    var buttonAC: MaterialButton? = null
    var buttonDot: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTv = findViewById(R.id.result_tv)
        solutionTv = findViewById(R.id.solution_tv)
        assignId(buttonC, R.id.button_c)
        assignId(buttonBrackOpen, R.id.button_open_bracket)
        assignId(buttonBrackClose, R.id.button_close_bracket)
        assignId(buttonDivide, R.id.button_divide)
        assignId(buttonMultiply, R.id.button_multiply)
        assignId(buttonPlus, R.id.button_plus)
        assignId(buttonMinus, R.id.button_minus)
        assignId(buttonEquals, R.id.button_equals)
        assignId(button0, R.id.button_0)
        assignId(button1, R.id.button_1)
        assignId(button2, R.id.button_2)
        assignId(button3, R.id.button_3)
        assignId(button4, R.id.button_4)
        assignId(button5, R.id.button_5)
        assignId(button6, R.id.button_6)
        assignId(button7, R.id.button_7)
        assignId(button8, R.id.button_8)
        assignId(button9, R.id.button_9)
        assignId(buttonAC, R.id.button_ac)
        assignId(buttonDot, R.id.button_dot)
    }

    private fun assignId(btn: MaterialButton?, id: Int) {
        var btn = btn
        btn = findViewById(id)
        btn.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = solutionTv!!.text.toString()

        if (buttonText == "AC") {
            solutionTv!!.text = ""
            resultTv!!.text = "0"
            return
        }

        if (buttonText == "=") {
            solutionTv!!.text = resultTv!!.text
            return
        }

        dataToCalculate = if (buttonText == "C") {
            dataToCalculate.substring(0, dataToCalculate.length - 1)
        }

        else {
            dataToCalculate + buttonText
        }

        solutionTv!!.text = dataToCalculate
        val finalResult = getResult(dataToCalculate)
        if (finalResult != "Err") {
            resultTv!!.text = finalResult
        }

    }

    private fun getResult(data: String?): String {
        return try {

            val context: Context = Context.enter()
            context.optimizationLevel = -1

            val scriptable: Scriptable = context.initStandardObjects()

            var finalResult: String =
                context.evaluateString(scriptable, data, "Javascript", 1, null).toString()

            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.replace(".0", "")
            }
            finalResult

        } catch (e: Exception) {
            "Err"
        }
    }
}