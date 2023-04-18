package aposalo.com.currencycalculator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import aposalo.com.currencycalculator.databinding.CountryListItemsBinding
import aposalo.com.currencycalculator.domain.model.CountrySymbols
import io.sentry.Sentry

class CountriesAdapter(private val onItemClick: (CountrySymbols) -> Unit): RecyclerView.Adapter<CountriesAdapter.PageViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CountrySymbols>() {

        override fun areItemsTheSame(oldItem: CountrySymbols, newItem: CountrySymbols): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: CountrySymbols, newItem: CountrySymbols): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    private var itemModels: List<CountrySymbols>
        get() = differ.currentList
        set(value) { differ.submitList(value) }

    override fun getItemCount(): Int {
        return itemModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = CountryListItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        try{
            with(holder){
                with(itemModels[position]){
                    binding.tvSymbol.text = this.symbol
                    binding.tvName.text = this.name
                    binding.listItem.setOnClickListener {
                        onItemClick(this)
                    }
                }
            }
        }
        catch(e: Exception){
            e.message?.let { Sentry.captureMessage(it) }
        }

    }

    inner class PageViewHolder(val binding: CountryListItemsBinding): ViewHolder(binding.root)
}