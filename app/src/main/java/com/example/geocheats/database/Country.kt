package com.example.geocheats.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "country_table")
data class Country(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,

        @ColumnInfo
        var name: String,
        @ColumnInfo
        var confidence: Float)

