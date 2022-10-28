package android.example.com.currencycalculator

import android.example.com.currencycalculator.model.CurrencyCalculatorModel
import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Resource
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable


const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var currencyArray: Array<String>

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var resultTv: TextView
    private lateinit var solutionTv: TextView
    private lateinit var currencyTv: TextView

    private lateinit var resultSpinner: Spinner
    private lateinit var currencySpinner: Spinner

    private lateinit var x: MaterialButton
    private lateinit var openBracket: MaterialButton
    private lateinit var closeBracket: MaterialButton
    private lateinit var divide: MaterialButton
    private lateinit var multiply: MaterialButton
    private lateinit var plus: MaterialButton
    private lateinit var minus: MaterialButton
    private lateinit var equals: MaterialButton
    private lateinit var zero: MaterialButton
    private lateinit var one: MaterialButton
    private lateinit var two: MaterialButton
    private lateinit var three: MaterialButton
    private lateinit var four: MaterialButton
    private lateinit var five: MaterialButton
    private lateinit var six: MaterialButton
    private lateinit var seven: MaterialButton
    private lateinit var eight: MaterialButton
    private lateinit var nine: MaterialButton
    private lateinit var c: MaterialButton
    private lateinit var dot: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currencyArray= resources.getStringArray(R.array.currency_array)
        resultTv = findViewById(R.id.result_tv)
        solutionTv = findViewById(R.id.solution_tv)
        currencyTv = findViewById(R.id.currency_tv)
        resultSpinner = setSpinner(R.id.result_spinner, 1)
        currencySpinner = setSpinner(R.id.currency_spinner, 0)

        val repository = CurrencyCalculatorRepository()
        viewModel = CurrencyCalculatorModel(repository)
        viewModel.data.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    if (response.data == null)
                        currencyTv.text = resources.getString(R.string.init_value)
                    else if (viewModel.isCurrentPage())
                        currencyTv.text = toTwoDecimals(response.data.result.toString())
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    currencyTv.text = resources.getString(R.string.loader)
                }
            }
        }

        resultTv.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val textChanged = s.toString()
                updateCurrency(textChanged)
            }
        })

        x = assignId(R.id.x)
        openBracket = assignId(R.id.open_bracket)
        closeBracket = assignId(R.id.close_bracket)
        divide = assignId(R.id.divide)
        multiply = assignId(R.id.multiply)
        plus = assignId(R.id.plus)
        minus = assignId(R.id.minus)
        equals = assignId(R.id.equals)
        zero = assignId(R.id.zero)
        one = assignId(R.id.one)
        two = assignId(R.id.two)
        three = assignId(R.id.three)
        four = assignId(R.id.four)
        five = assignId(R.id.five)
        six = assignId(R.id.six)
        seven = assignId(R.id.seven)
        eight = assignId(R.id.eight)
        nine = assignId(R.id.nine)
        c = assignId(R.id.c)
        dot = assignId(R.id.dot)
    }

    private fun assignId(id: Int): MaterialButton {
        val btn: MaterialButton = findViewById(id)
        btn.setOnClickListener(this)
        return btn
    }

    private fun setSpinner(id: Int, selection : Int): Spinner {
        val spinner : Spinner = findViewById(id)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyArray)
        spinner.onItemSelectedListener = this;
        spinner.setSelection(selection)
        return spinner
    }

    private fun updateCurrency(result: String) {
        val fromSelectedItem = currencyArray[resultSpinner.selectedItemId.toInt()]
        val toSelectedItem = currencyArray[currencySpinner.selectedItemId.toInt()]
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, result.toFloat())
    }

    private fun clearCalculator() {
        solutionTv.text = ""
        resultTv.text = resources.getString(R.string.init_value)
    }

    private fun toTwoDecimals(num: String): String {
        val number: Float = num.toFloat()
        val solution: Float = String.format("%.2f", number).toFloat()

        var solutionString = solution.toString()

        if (solutionString.endsWith(".0")) {
            solutionString = solutionString.replace(".0", "")
        }
        return solutionString
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = solutionTv.text.toString()

        when (buttonText) {
            resources.getString(R.string.clear_button) -> {
                clearCalculator()
                return
            }
            resources.getString(R.string.equal_button) -> {
                solutionTv.text = resultTv.text
                return
            }
            resources.getString(R.string.delete_button) -> {
                if (solutionTv.text != "") {
                    dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length - 1)
                    if (dataToCalculate == "") {
                        clearCalculator()
                        return
                    }
                }
                else {
                    clearCalculator()
                    return
                }
            }
            else -> {
                dataToCalculate += buttonText
            }
        }
        solutionTv.text = dataToCalculate

        val finalResult = getResult(dataToCalculate)

        if (finalResult != "Err") {
            resultTv.text = finalResult
        }
    }

    private fun getResult(data: String): String {
        return try {
            var newData = data

            while (newData.startsWith("0") && newData.length > 1)
                newData = newData.substring(1)

            val context: Context = Context.enter()
            context.optimizationLevel = -1
            val scriptable: Scriptable = context.initStandardObjects()
            val res = context.evaluateString(scriptable, newData, "Javascript", 1, null).toString()
            toTwoDecimals(res)
        }
        catch (e: Exception) {
            "Err"
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        updateCurrency(resultTv.text.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}