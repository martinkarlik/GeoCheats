package com.example.geocheats.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Guess::class], version = 1, exportSchema = false)
abstract class GuessDatabase : RoomDatabase() {

    abstract val dao: GuessDao

    companion object {
        @Volatile
        private var INSTANCE: GuessDatabase? = null

        fun getInstance(context: Context) : GuessDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, GuessDatabase::class.java, "guess_database").fallbackToDestructiveMigration().build()
                }

                return instance
            }
        }
    }


}