package aposalo.com.currencycalculator.util

class RateConvertor {

    companion object{
        fun getConvertedQuotes(quotes:Map<String,Float>, source:String):Map<String,Float> {
            val convertedQuotes = mutableMapOf<String,Float> ()
            quotes.forEach { (k,v) ->
                val newKey = k.removePrefix(source)
                convertedQuotes[newKey] = v
            }
            return convertedQuotes;
        }
    }

}