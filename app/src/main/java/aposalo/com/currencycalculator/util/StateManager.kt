package aposalo.com.currencycalculator.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.domain.model.CurrentCurrencies
import aposalo.com.currencycalculator.util.Constants.Companion.CURRENCY_TEXT_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RESULT_TEXT_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.RESULT_TEXT_VALUE_LABEL
import aposalo.com.currencycalculator.util.Constants.Companion.SHARED_PREF
import aposalo.com.currencycalculator.util.Constants.Companion.SOLUTION_TEXT_LABEL
import com.google.gson.Gson

class StateManager (private val context : Context) {

     private lateinit var resources : Resources
     private lateinit var binding : ActivityMainBinding

    constructor (
        resources : Resources,
        context : Context) : this(context) {
            this.resources = resources
    }

     constructor (
         resources : Resources,
         context : Context,
         binding : ActivityMainBinding) : this(resources, context) {
         this.binding = binding
     }

    fun saveLastState() {
        val sharedPreferences : SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString(CURRENCY_TEXT_LABEL, binding.currencyText.text.toString())
        myEdit.putString(RESULT_TEXT_LABEL, binding.resultText.text.toString())
        myEdit.putString(SOLUTION_TEXT_LABEL, binding.solutionTv.text.toString())
        myEdit.putString(RESULT_TEXT_VALUE_LABEL, binding.resultTv.text.toString())
        myEdit.apply()
    }

    fun restoreLastState() {
        val sh = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        binding.currencyText.text = sh.getString(CURRENCY_TEXT_LABEL, resources.getString(R.string.GBP))!!
        binding.resultText.text = sh.getString(RESULT_TEXT_LABEL, resources.getString(R.string.EUR))!!
        binding.solutionTv.text = sh.getString(SOLUTION_TEXT_LABEL, String())
        binding.resultTv.text = sh?.getString(RESULT_TEXT_VALUE_LABEL, resources.getString(R.string.init_value))
    }

    fun updateCountryValue(layout: String, newValue: String) {
        val sharedPreferences : SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit : SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString(layout,newValue)
        myEdit.apply()
    }

    fun getCurrentCurrencies(): String {
        val sh = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val from = sh.getString(RESULT_TEXT_LABEL, String())!!
        val to = sh.getString(CURRENCY_TEXT_LABEL, String())!!
        return Gson().toJson(CurrentCurrencies(from = from, to = to))
    }

}