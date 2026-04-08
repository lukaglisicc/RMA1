package com.example.rma1

import android.app.Application
import com.example.rma1.di.initKoin
import org.koin.android.ext.koin.androidContext


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
        }
    }
}