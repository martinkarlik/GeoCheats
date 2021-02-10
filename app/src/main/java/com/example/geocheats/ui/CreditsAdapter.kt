package com.example.geocheats.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.geocheats.R
import com.example.geocheats.database.Country
import com.example.geocheats.databinding.ListItemCountryBinding
import kotlinx.android.synthetic.main.fragment_credits.view.*
import kotlinx.android.synthetic.main.list_item_country.view.*

// To implement recycler view with multiple view types (to add a header for example) and more:
// https://github.com/udacity/andfun-kotlin-sleep-tracker-with-recyclerview

class CreditsAdapter(val clickListener: CountryClickListener) : ListAdapter<Country, CustomViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class CustomViewHolder private constructor(val binding: ListItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemCountryBinding.inflate(layoutInflater, parent, false)
            return CustomViewHolder(binding)
        }
    }

    fun bind(item: Country, clickListener: CountryClickListener) {
        binding.country = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}


class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
    override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem == newItem
    }
}

class CountryClickListener(val clickListener: (countryId: Long) -> Unit) {
    fun onClick(country: Country) = clickListener(country.id)

}