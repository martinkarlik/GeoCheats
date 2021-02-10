package com.example.geocheats

import android.app.Application
import timber.log.Timber

class GeoCheatsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}