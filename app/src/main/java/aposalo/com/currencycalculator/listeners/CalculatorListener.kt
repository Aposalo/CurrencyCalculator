package aposalo.com.currencycalculator.listeners

import android.content.res.Resources
import android.view.View
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.util.CalculationExtensions.Companion.getCalculation
import com.google.android.material.button.MaterialButton

class CalculatorListener(private var binding: ActivityMainBinding,
                         private var resources: Resources) : View.OnClickListener {

    private fun clearCalculator() {
        binding.solutionTv.text = String()
        binding.resultTv.text = resources.getString(R.string.init_value)
    }

    fun setOnClickListenerButtons(){
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

    override fun onClick(view: View?) {
        val button = view as MaterialButton
        val buttonText = button.text.toString()
        var dataToCalculate = binding.solutionTv.text.toString()
        val result = binding.resultTv.text.toString()
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
        val finalResult = dataToCalculate.getCalculation()

        if (finalResult != "Err" && finalResult != result) {
            binding.resultTv.text = finalResult
        }
    }
}