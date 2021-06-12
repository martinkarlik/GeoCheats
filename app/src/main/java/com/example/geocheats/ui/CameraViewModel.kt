package com.example.geocheats.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.geocheats.database.Guess
import com.example.geocheats.database.GuessDao
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class CameraViewModel(val database: GuessDao, application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _guess: MutableLiveData<Guess?> by lazy {
        MutableLiveData<Guess?>()
    }
    val guess: LiveData<Guess?>
        get() = _guess

    private val _capturedImage: MutableLiveData<Bitmap?> by lazy {
        MutableLiveData<Bitmap?>()
    }
    val capturedImage: LiveData<Bitmap?>
        get() = _capturedImage

    private val _state: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }
    val state: LiveData<String?>
        get() = _state



    fun onCapture(bitmap: Bitmap) {
        _capturedImage.value = bitmap

    }

    fun onGuessed(latLng: Pair<Double, Double>, confidence: Float) {
        _state.value = "MAP"
        _guess.value = Guess(lat = latLng.first, lng = latLng.second, confidence = confidence)


        uiScope.launch {

            withContext(Dispatchers.IO) {
                database.insert(_guess.value!!)
            }
        }
    }

    fun onReturnToPreview() {
        _state.value = "CAMERA"
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}