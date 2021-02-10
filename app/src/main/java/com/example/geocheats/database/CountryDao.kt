package com.example.geocheats.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CountryDao {


    @Insert
    fun insert(country: Country)

    @Update
    fun update(country: Country)

    @Query("SELECT * FROM country_table WHERE id == :key")
    fun get(key: Long) : Country

    @Query("DELETE FROM country_table")
    fun clear()

    @Query("SELECT * FROM country_table ORDER BY id DESC")
    fun getAll() : List<Country>

    @Query("SELECT * FROM country_table ORDER BY id DESC LIMIT 1")
    fun getLast() : Country?




}