package com.example.geocheats.network

import com.squareup.moshi.Json

data class MarsProperty(
    val id: String,
    val type: String,
    @Json(name = "img_src") val imgSrc: String,
    val price: Double
)