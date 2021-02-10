package com.example.geocheats.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geocheats.database.Country
import com.example.geocheats.database.CountryDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import timber.log.Timber

class CameraViewModel(val database: CountryDao, application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _country: MutableLiveData<Country?> by lazy {
        MutableLiveData<Country?>()
    }
    val country: LiveData<Country?>
        get() = _country

    private val _capturedImage: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>()
    }
    val capturedImage: LiveData<Bitmap?>
        get() = _capturedImage


    fun onCapture(bitmap: Bitmap) {
        _capturedImage.value = bitmap

    }

    fun onCountryGuessed(name: String, confidence: Float) {
        _country.value = Country(name = name, confidence = confidence)

        uiScope.launch {

            withContext(Dispatchers.IO) {
                database.insert(_country.value!!)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}