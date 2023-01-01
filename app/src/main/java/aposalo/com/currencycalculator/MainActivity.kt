package aposalo.com.currencycalculator

import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.util.Extensions.Companion.getCalculation
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.faendir.rhino_android.RhinoAndroidHelper
import com.google.android.material.button.MaterialButton

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var currencyArray: Array<String>

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

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
        val fromSelectedItem = currencyArray[binding.resultSpinner.selectedItemId.toInt()]
        val toSelectedItem = currencyArray[binding.currencySpinner.selectedItemId.toInt()]
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
        val result = binding.resultTv.text.toString();
        when (buttonText) {
            resources.getString(R.string.clear_button) -> {
                clearCalculator()
                return
            }
            resources.getString(R.string.equal_button) -> {
                binding.solutionTv.text = result
                return
            }
            resources.getString(R.string.delete_button) -> {
                if (!binding.solutionTv.text.isNullOrEmpty()) {
                    dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length - 1)
                    if (dataToCalculate.isEmpty()) {
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
        val finalResult = dataToCalculate.getCalculation(RhinoAndroidHelper(this))

        if (finalResult != "Err" && finalResult != result) {
            binding.resultTv.text = finalResult
        }
    }
}