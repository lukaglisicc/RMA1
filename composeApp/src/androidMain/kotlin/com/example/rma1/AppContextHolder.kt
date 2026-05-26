package com.example.rma1

import android.content.Context

object AppContextHolder {

    lateinit var appContext: Context
        private set

    @Synchronized
    fun init(context: Context) {
        if (isInitialized) return
        appContext = context.applicationContext
    }

    val isInitialized: Boolean
        get() = ::appContext.isInitialized
}