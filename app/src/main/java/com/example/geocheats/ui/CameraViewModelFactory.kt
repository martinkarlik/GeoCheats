package com.example.geocheats.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.geocheats.database.GuessDao
import java.lang.IllegalArgumentException

class CameraViewModelFactory(
        private val dataSource: GuessDao,
        private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Uknown ViewModel class.")
    }
}