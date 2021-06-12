package com.example.geocheats.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.geocheats.database.Guess
import com.example.geocheats.databinding.ListItemGuessBinding

// To implement recycler view with multiple view types (to add a header for example) and more:
// https://github.com/udacity/andfun-kotlin-sleep-tracker-with-recyclerview

class CreditsAdapter(val clickListener: GuessClickListener) : ListAdapter<Guess, CustomViewHolder>(GuessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

class CustomViewHolder private constructor(val binding: ListItemGuessBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemGuessBinding.inflate(layoutInflater, parent, false)
            return CustomViewHolder(binding)
        }
    }

    fun bind(item: Guess, clickListener: GuessClickListener) {
        binding.guess = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }
}


class GuessDiffCallback : DiffUtil.ItemCallback<Guess>() {
    override fun areItemsTheSame(oldItem: Guess, newItem: Guess): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Guess, newItem: Guess): Boolean {
        return oldItem == newItem
    }
}

class GuessClickListener(val clickListener: (countryId: Long) -> Unit) {
    fun onClick(country: Guess) = clickListener(country.id)

}