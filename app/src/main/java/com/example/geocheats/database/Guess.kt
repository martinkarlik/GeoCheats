package com.example.geocheats.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng


@Entity(tableName = "guess_table")
data class Guess(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,

        @ColumnInfo
        var lat: Double,
        @ColumnInfo
        var lng: Double,
        @ColumnInfo
        var confidence: Float)

