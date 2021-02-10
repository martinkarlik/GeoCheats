package com.example.geocheats.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.geocheats.R
import com.example.geocheats.database.Country

@BindingAdapter("country_name")
fun TextView.setCountryName(item: Country?) {
    item?.let {
        text = item.name
    }
}


@BindingAdapter("img_url")
fun ImageView.setImgUrl(url: String?) {
    url?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_baseline_bar_chart_24))
                .into(this)
    }
}