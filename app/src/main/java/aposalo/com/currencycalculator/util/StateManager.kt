package aposalo.com.currencycalculator.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RATE_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RESULT_TEXT_VALUE_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.SHARED_PREF
import aposalo.com.currencycalculator.util.Constants.Companion.SOLUTION_TEXT_LABEL

class StateManager(private val resources: Resources,
                   private val context : Context) {

     private lateinit var binding: ActivityMainBinding

    fun setBinding(binding: ActivityMainBinding){
        this.binding = binding
    }

    fun saveLastState(){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString(CURRENCY_TEXT_LABEL,binding.currencyText.text.toString())
        myEdit.putString(RESULT_TEXT_LABEL,binding.resultText.text.toString())
        myEdit.putString(SOLUTION_TEXT_LABEL,binding.solutionTv.text.toString())
        myEdit.putString(RESULT_TEXT_VALUE_LABEL,binding.resultTv.text.toString())
        myEdit.apply()
    }

    fun restoreLastState(){
        val sh = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        binding.currencyText.text = sh.getString(CURRENCY_TEXT_LABEL,resources.getString(R.string.GBP))!!
        binding.resultText.text = sh.getString(RESULT_TEXT_LABEL,resources.getString(R.string.EUR))!!
        binding.solutionTv.text = sh.getString(SOLUTION_TEXT_LABEL,String())
        binding.resultTv.text = sh?.getString(RESULT_TEXT_VALUE_LABEL,resources.getString(R.string.init_value))
    }

    fun updateCountryValue(layout: String, newValue: String){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString(layout,newValue)
        myEdit.apply()
    }

    fun updateRate(rate : String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString(RATE_LABEL, rate)
        myEdit.apply()
    }

    fun getRate() : String? {
        val sh = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        return sh.getString(RATE_LABEL, "1.0f")
    }

}