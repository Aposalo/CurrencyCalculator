package android.example.com.currencycalculator

import android.example.com.currencycalculator.databinding.ActivityMainBinding
import android.example.com.currencycalculator.model.CurrencyCalculatorModel
import android.example.com.currencycalculator.util.Extensions.Companion.getCalculation
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var currencyArray: Array<String>

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private var isLastCharacterDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currencyArray = resources.getStringArray(R.array.currency_array)
        viewModel = CurrencyCalculatorModel(binding, resources)

        binding.resultTv.addTextChangedListener(object : TextWatcher {
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
        binding.resultSpinner.setSpinner(1)
        binding.currencySpinner.setSpinner(0)

        binding.x.setOnClickListener(this)
        binding.openBracket.setOnClickListener(this)
        binding.closeBracket.setOnClickListener(this)
        binding.divide.setOnClickListener(this)
        binding.multiply.setOnClickListener(this)
        binding.plus.setOnClickListener(this)
        binding.minus.setOnClickListener(this)
        binding.equals.setOnClickListener(this)
        binding.zero.setOnClickListener(this)
        binding.one.setOnClickListener(this)
        binding.two.setOnClickListener(this)
        binding.three.setOnClickListener(this)
        binding.four.setOnClickListener(this)
        binding.five.setOnClickListener(this)
        binding.six.setOnClickListener(this)
        binding.seven.setOnClickListener(this)
        binding.eight.setOnClickListener(this)
        binding.nine.setOnClickListener(this)
        binding.c.setOnClickListener(this)
        binding.dot.setOnClickListener(this)
    }


    private fun Spinner.setSpinner(selection : Int) {
        this.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, currencyArray)
        this.setSelection(selection)
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateCurrency(binding.resultTv.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateCurrency(result: String) {
        if (!isLastCharacterDot) {
            val fromSelectedItem = currencyArray[binding.resultSpinner.selectedItemId.toInt()]
            val toSelectedItem = currencyArray[binding.currencySpinner.selectedItemId.toInt()]
            viewModel.getUserPage(toSelectedItem, fromSelectedItem, result.toFloat())
            isLastCharacterDot = false
        }
    }

    private fun clearCalculator() {
        binding.solutionTv.text = ""
        binding.resultTv.text = resources.getString(R.string.init_value)
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = binding.solutionTv.text.toString()
        isLastCharacterDot = false
        when (buttonText) {
            resources.getString(R.string.clear_button) -> {
                clearCalculator()
                return
            }
            resources.getString(R.string.equal_button) -> {
                binding.solutionTv.text = binding.resultTv.text
                return
            }
            resources.getString(R.string.delete_button) -> {
                if (!binding.solutionTv.text.isNullOrEmpty()) {
                    val lastCharacter = dataToCalculate.takeLast(1)//dataToCalculate.substring(dataToCalculate.length - 1, dataToCalculate.length)
                    dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length - 1)
                    if (dataToCalculate.isEmpty()) {
                        clearCalculator()
                        return
                    }
                    else if (lastCharacter == resources.getString(R.string.dot_button))
                        isLastCharacterDot = true
                }
                else {
                    clearCalculator()
                    return
                }
            }
            resources.getString(R.string.dot_button) -> {
                isLastCharacterDot = true
                dataToCalculate += buttonText
            }
            else -> {
                dataToCalculate += buttonText
            }
        }
        binding.solutionTv.text = dataToCalculate

        val finalResult = dataToCalculate.getCalculation()

        if (finalResult != "Err") {
            binding.resultTv.text = finalResult
        }
    }
}