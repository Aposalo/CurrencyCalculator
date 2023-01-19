package aposalo.com.currencycalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.listeners.CalculatorListener
import aposalo.com.currencycalculator.model.CurrencyCalculatorModel

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

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

        val buttonListener = CalculatorListener(binding, resources)

        binding.x.setOnClickListener(buttonListener)
        binding.openBracket.setOnClickListener(buttonListener)
        binding.closeBracket.setOnClickListener(buttonListener)
        binding.divide.setOnClickListener(buttonListener)
        binding.multiply.setOnClickListener(buttonListener)
        binding.plus.setOnClickListener(buttonListener)
        binding.minus.setOnClickListener(buttonListener)
        binding.equals.setOnClickListener(buttonListener)
        binding.zero.setOnClickListener(buttonListener)
        binding.one.setOnClickListener(buttonListener)
        binding.two.setOnClickListener(buttonListener)
        binding.three.setOnClickListener(buttonListener)
        binding.four.setOnClickListener(buttonListener)
        binding.five.setOnClickListener(buttonListener)
        binding.six.setOnClickListener(buttonListener)
        binding.seven.setOnClickListener(buttonListener)
        binding.eight.setOnClickListener(buttonListener)
        binding.nine.setOnClickListener(buttonListener)
        binding.c.setOnClickListener(buttonListener)
        binding.dot.setOnClickListener(buttonListener)
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
                val textChanged = binding.resultTv.text.toString()
                updateCurrency(textChanged)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateCurrency(result: String) {
        val fromSelectedItem = currencyArray[binding.resultSpinner.selectedItemId.toInt()]
        val toSelectedItem = currencyArray[binding.currencySpinner.selectedItemId.toInt()]
        val floatResult = result.toFloatOrNull() ?: 0.0f
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, floatResult)
    }
}