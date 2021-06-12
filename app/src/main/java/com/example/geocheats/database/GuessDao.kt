package com.example.geocheats.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GuessDao {


    @Insert
    fun insert(country: Guess)

    @Update
    fun update(country: Guess)

    @Query("SELECT * FROM guess_table WHERE id == :key")
    fun get(key: Long) : Guess

    @Query("DELETE FROM guess_table")
    fun clear()

    @Query("SELECT * FROM guess_table ORDER BY id DESC")
    fun getAll() : List<Guess>

    @Query("SELECT * FROM guess_table ORDER BY id DESC LIMIT 1")
    fun getLast() : Guess?




}