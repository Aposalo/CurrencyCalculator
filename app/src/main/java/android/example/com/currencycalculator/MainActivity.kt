package android.example.com.currencycalculator

import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Resource
import android.example.com.userleaderboard.model.CurrencyCalculatorModel
import android.os.Bundle
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

    var x: MaterialButton? = null
    var openBracket: MaterialButton? = null
    var closeBracket: MaterialButton? = null
    var divide: MaterialButton? = null
    var multiply: MaterialButton? = null
    var plus: MaterialButton? = null
    var minus: MaterialButton? = null
    var equals: MaterialButton? = null
    var zero: MaterialButton? = null
    var one: MaterialButton? = null
    var two: MaterialButton? = null
    var three: MaterialButton? = null
    var four: MaterialButton? = null
    var five: MaterialButton? = null
    var six: MaterialButton? = null
    var seven: MaterialButton? = null
    var eight: MaterialButton? = null
    var nine: MaterialButton? = null
    var c: MaterialButton? = null
    var dot: MaterialButton? = null

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
        viewModel.pageData.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { fixerResponse ->
                        currencyTv.text = fixerResponse.result.toString()
                        //userLeaderboardAdapter.differ.submitList(viewModel.getItemModels())
                        //isLastPage = viewModel.currentPageNumber == fixerResponse.totalPages
                    }
                }
                is Resource.Error -> {
                    //hideProgressBar()

                    response.message?.let { message ->
                        currencyTv.text = message
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    currencyTv.text = "..."
                    //showProgressBar()
                }
            }
        }

        assignId(x, R.id.x)
        assignId(openBracket, R.id.open_bracket)
        assignId(closeBracket, R.id.close_bracket)
        assignId(divide, R.id.divide)
        assignId(multiply, R.id.multiply)
        assignId(plus, R.id.plus)
        assignId(minus, R.id.minus)
        assignId(equals, R.id.equals)
        assignId(zero, R.id.zero)
        assignId(one, R.id.one)
        assignId(two, R.id.two)
        assignId(three, R.id.three)
        assignId(four, R.id.four)
        assignId(five, R.id.five)
        assignId(six, R.id.six)
        assignId(seven, R.id.seven)
        assignId(eight, R.id.eight)
        assignId(nine, R.id.nine)
        assignId(c, R.id.c)
        assignId(dot, R.id.dot)

    }

    private fun assignId(btn: MaterialButton?, id: Int) {
        var btn = btn
        btn = findViewById(id)
        btn.setOnClickListener(this)
    }

    private fun setSpinner(id: Int, selection : Int): Spinner {
        val spinner : Spinner = findViewById(id)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyArray)
        spinner.onItemSelectedListener = this;
        spinner.setSelection(selection)
        return spinner
    }

    private fun clearCalculator() {
        solutionTv.text = ""
        resultTv.text = "0"
        currencyTv.text = "0"
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = solutionTv.text.toString()

        when (buttonText) {
            "C" -> {
                clearCalculator()
                return
            }
            "=" -> {
                val fromSelectedItem = currencyArray[resultSpinner.selectedItemId.toInt()]
                val toSelectedItem = currencyArray[currencySpinner.selectedItemId.toInt()]
                viewModel.getUserPage(toSelectedItem, fromSelectedItem, resultTv.text.toString().toInt())
                solutionTv.text = resultTv.text
                return
            }
            "X" -> {
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
            val fromSelectedItem = currencyArray[resultSpinner.selectedItemId.toInt()]
            val toSelectedItem = currencyArray[currencySpinner.selectedItemId.toInt()]
            viewModel.getUserPage(toSelectedItem, fromSelectedItem,resultTv.text.toString().toInt())
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val fromSelectedItem = currencyArray[resultSpinner.selectedItemId.toInt()]
        val toSelectedItem = currencyArray[currencySpinner.selectedItemId.toInt()]
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, resultTv.text.toString().toInt())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}