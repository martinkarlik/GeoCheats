package com.example.geocheats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.geocheats.R
import com.example.geocheats.database.Country
import com.example.geocheats.databinding.FragmentCreditsBinding

class CreditsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding : FragmentCreditsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_credits, container, false)

//        // To use other than linear layout manager:
//        val layoutManager = GridLayoutManager(activity, 3)
//        binding.creditsList.layoutManager = layoutManager
//
//        // We can also specify spans of the grid, for example if the header should take the entire row:
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int) =  when (position) {
//                0 -> 3
//                else -> 1
//            }
//        }

        val adapter = CreditsAdapter(CountryClickListener {
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
        })
        binding.creditsList.adapter = adapter

        adapter.submitList(
            listOf(
                Country(name = "Argentina", confidence = 1.0f),
                Country(name = "DRC", confidence = 1.0f),
                Country(name = "Rwanda", confidence = 1.0f),
                Country(name = "Nepal", confidence = 1.0f),
                Country(name = "Sri Lanka", confidence = 1.0f),
                Country(name = "Brazil", confidence = 1.0f),
                Country(name = "Malawi", confidence = 1.0f),
                Country(name = "Eswatini", confidence = 1.0f),
            )
        )


        return binding.root
    }
}