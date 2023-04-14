package aposalo.com.currencycalculator.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import aposalo.com.currencycalculator.R
import aposalo.com.currencycalculator.databinding.ActivityMainBinding
import aposalo.com.currencycalculator.util.Constants.Companion.SHARED_PREF

class ActivityMainStateManager(private val resources: Resources,
                               private val context : Context) {

     private lateinit var binding: ActivityMainBinding

    fun setBinding(binding: ActivityMainBinding){
        this.binding = binding
    }

    fun saveLastState(){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString("currencySpinner",binding.currencyButton.text.toString())
        myEdit.putString("resultSpinner",binding.resultText.text.toString())
        myEdit.putString("solutionTv",binding.solutionTv.text.toString())
        myEdit.putString("resultTv",binding.resultTv.text.toString())
        myEdit.apply()
    }

    fun updateCurrencyValue(newValue: String){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString("currencySpinner",newValue)
        myEdit.apply()
    }

    fun updateResultValue(newValue: String){
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sharedPreferences.edit()
        myEdit.putString("resultSpinner",newValue)
        myEdit.apply()
    }

    fun restoreLastState(){
        val sh = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        binding.currencyButton.text = sh.getString("currencySpinner",resources.getString(R.string.GBP))!!
        binding.resultText.text = sh.getString("resultSpinner",resources.getString(R.string.EUR))!!
        binding.solutionTv.text = sh.getString("solutionTv","")
        binding.resultTv.text = sh?.getString("resultTv",resources.getString(R.string.init_value))
    }
}