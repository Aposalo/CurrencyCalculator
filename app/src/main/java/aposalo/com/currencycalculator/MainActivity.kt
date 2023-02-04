package aposalo.com.currencycalculator

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.local.AppDatabase
import aposalo.com.currencycalculator.domain.model.CurrencyCalculatorModel
import aposalo.com.currencycalculator.listeners.CalculatorListener


const val TAG = "MainActivity"
const val SHARED_PREF = "currency_calculator"

class MainActivity : AppCompatActivity() {

    private lateinit var currencyArray: Array<String>

    private lateinit var viewModel: CurrencyCalculatorModel

    private lateinit var binding: ActivityMainBinding

    private var mDb: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currencyArray = resources.getStringArray(R.array.currency_array)
        mDb = AppDatabase.getInstance(applicationContext)
        viewModel = CurrencyCalculatorModel(binding, resources, mDb)
        binding.resultTv.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                s.toString().updateCurrency()
            }
        })
        restoreLastState()
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        saveLastState()
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restoreLastState()
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun saveLastState(){
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(SHARED_PREF, MODE_PRIVATE)

        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()

        myEdit.putString("currencySpinner",currencyArray[binding.currencySpinner.selectedItemId.toInt()])
        myEdit.putString("resultSpinner",currencyArray[binding.resultSpinner.selectedItemId.toInt()])
        myEdit.putString("solutionTv",binding.solutionTv.text.toString())
        myEdit.putString("resultTv",binding.resultTv.text.toString())
        myEdit.apply()
    }

    private fun restoreLastState(){
        val sh = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        binding.resultSpinner.setSpinner(sh.getString("resultSpinner",resources.getString(R.string.EUR))!!)
        binding.currencySpinner.setSpinner(sh.getString("currencySpinner",resources.getString(R.string.GBP))!!)
        binding.solutionTv.text = sh.getString("solutionTv","")
        binding.resultTv.text = sh?.getString("resultTv",resources.getString(R.string.init_value))
    }

    override fun onDestroy() {
        saveLastState()
        super.onDestroy()
    }

    private fun Spinner.setSpinner(selection : String) {
        val currencySelection = currencyArray.indexOf(selection)
        this.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, currencyArray)
        this.setSelection(currencySelection)
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.resultTv.text.toString().updateCurrency()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun String.updateCurrency() {
        val toSelectedItem = currencyArray[binding.currencySpinner.selectedItemId.toInt()]
        val fromSelectedItem = currencyArray[binding.resultSpinner.selectedItemId.toInt()]
        val floatResult = this.toFloatOrNull() ?: 0.0f
        viewModel.getUserPage(toSelectedItem, fromSelectedItem, floatResult)
    }
}