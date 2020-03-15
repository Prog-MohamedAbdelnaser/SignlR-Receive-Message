package com.tabeesgl

import android.app.Application
import androidx.multidex.MultiDexApplication

class ApplicationClass: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
    }
}