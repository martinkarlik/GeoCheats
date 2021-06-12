package com.example.geocheats.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.geocheats.R
import com.example.geocheats.database.Guess
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

        val adapter = CreditsAdapter(GuessClickListener {
            Toast.makeText(context, "${it}", Toast.LENGTH_SHORT).show()
        })
        binding.creditsList.adapter = adapter

        adapter.submitList(
            listOf(
                Guess(lat = 34.2, lng = 41.5, confidence = 1.0f),
                Guess(lat = 3.2, lng = 31.5, confidence = 1.0f),
                Guess(lat = 34.1, lng = 21.5, confidence = 1.0f),
                Guess(lat = 4.2, lng = 45.5, confidence = 1.0f),
                Guess(lat = 0.2, lng = 41.8, confidence = 1.0f),
                Guess(lat = -34.2, lng = 74.5, confidence = 1.0f),
                Guess(lat = -4.2, lng = -12.5, confidence = 1.0f),
                Guess(lat = -3.2, lng = 25.5, confidence = 1.0f)
            )
        )


        return binding.root
    }
}