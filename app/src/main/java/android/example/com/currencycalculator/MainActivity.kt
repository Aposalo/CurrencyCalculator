package android.example.com.currencycalculator

import android.example.com.currencycalculator.databinding.ActivityMainBinding
import android.example.com.currencycalculator.model.CurrencyCalculatorModel
import android.example.com.currencycalculator.repository.CurrencyCalculatorRepository
import android.example.com.currencycalculator.util.Extensions.Companion.getCalculation
import android.example.com.currencycalculator.util.Extensions.Companion.toTwoDecimals
import android.example.com.currencycalculator.util.Resource
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var currencyArray: Array<String>

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var resultSpinner: Spinner
    private lateinit var currencySpinner: Spinner

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        currencyArray= resources.getStringArray(R.array.currency_array)
        resultSpinner = setSpinner(R.id.result_spinner, 1)
        currencySpinner = setSpinner(R.id.currency_spinner, 0)

        val repository = CurrencyCalculatorRepository()
        val initAmount = resources.getString(R.string.init_value).toFloat()
        val initTo = resources.getString(R.string.GBP)
        val initFrom = resources.getString(R.string.EUR)
        viewModel = CurrencyCalculatorModel(repository, initAmount, initTo, initFrom)
        viewModel.data.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    (if (response.data == null)
                        resources.getString(R.string.init_value)
                    else
                        response.data.result.toString().toTwoDecimals()).
                    also { binding.currencyTv.text = it }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Log.e(TAG, "An error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    binding.currencyTv.text = resources.getString(R.string.loader)
                }
            }
        }

        binding.resultTv.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                updateCurrency(s.toString())
            }
        })

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

    private fun setSpinner(id: Int, selection : Int): Spinner {
        val spinner = findViewById<Spinner>(id)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyArray)
        spinner.onItemSelectedListener = this
        spinner.setSelection(selection)
        return spinner
    }

    private fun updateCurrency(result: String) {
        val fromSelectedItem = currencyArray[resultSpinner.selectedItemId.toInt()]
        val toSelectedItem = currencyArray[currencySpinner.selectedItemId.toInt()]
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, result.toFloat())
    }

    private fun clearCalculator() {
        binding.solutionTv.text = ""
        binding.resultTv.text = resources.getString(R.string.init_value)
    }

    override fun onClick(view: View) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = binding.solutionTv.text.toString()

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
                if (binding.solutionTv.text != "") {
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
        binding.solutionTv.text = dataToCalculate

        val finalResult = dataToCalculate.getCalculation()

        if (finalResult != "Err") {
            binding.resultTv.text = finalResult
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        updateCurrency(binding.resultTv.text.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}