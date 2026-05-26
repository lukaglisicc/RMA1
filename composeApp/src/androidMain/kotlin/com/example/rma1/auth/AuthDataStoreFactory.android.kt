package com.example.rma1.auth

import com.example.rma1.AppContextHolder

private const val AUTH_DATA_FILE_NAME = "auth_data.json"

actual fun createAuthDataStorePath(): String {
    val context = AppContextHolder.appContext
    return context.filesDir.resolve("datastore/$AUTH_DATA_FILE_NAME").absolutePath
}