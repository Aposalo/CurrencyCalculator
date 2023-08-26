package aposalo.com.currencycalculator.utils

import android.text.Editable
import android.text.TextWatcher

interface CurrencyCalculatorTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(s: Editable) {
    }
}