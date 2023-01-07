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

        val listener = CalculatorListener(binding, resources)

        binding.x.setOnClickListener(listener)
        binding.openBracket.setOnClickListener(listener)
        binding.closeBracket.setOnClickListener(listener)
        binding.divide.setOnClickListener(listener)
        binding.multiply.setOnClickListener(listener)
        binding.plus.setOnClickListener(listener)
        binding.minus.setOnClickListener(listener)
        binding.equals.setOnClickListener(listener)
        binding.zero.setOnClickListener(listener)
        binding.one.setOnClickListener(listener)
        binding.two.setOnClickListener(listener)
        binding.three.setOnClickListener(listener)
        binding.four.setOnClickListener(listener)
        binding.five.setOnClickListener(listener)
        binding.six.setOnClickListener(listener)
        binding.seven.setOnClickListener(listener)
        binding.eight.setOnClickListener(listener)
        binding.nine.setOnClickListener(listener)
        binding.c.setOnClickListener(listener)
        binding.dot.setOnClickListener(listener)
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

}