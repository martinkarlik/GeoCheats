package com.example.geocheats.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.geocheats.network.MarsApi
import com.example.geocheats.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _property = MutableLiveData<MarsProperty>()
    val property: LiveData<MarsProperty>
        get() = _property


    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    init {
        coroutineScope.launch {
            val propertiesDeferred = MarsApi.retrofitService.getProperties()
            try {
                val listResult = propertiesDeferred.await()
                _status.value = "Success: ${listResult.size}"
                if (listResult.size > 0) {
                    _property.value = listResult[0]
                }
            } catch (t: Throwable) {
                _status.value = "Failure: ${t.message}"
            }

        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}