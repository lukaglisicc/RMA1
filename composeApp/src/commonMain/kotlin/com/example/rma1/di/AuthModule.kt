package com.example.rma1.di

import androidx.datastore.core.DataStore
import com.example.rma1.auth.AuthStore
import com.example.rma1.auth.createAuthDataStore
import com.example.rma1.auth.model.AuthData
import org.koin.dsl.module

val authModule = module {

    single<DataStore<AuthData>> { createAuthDataStore() }

    single<AuthStore> { AuthStore(persistence = get()) }
}