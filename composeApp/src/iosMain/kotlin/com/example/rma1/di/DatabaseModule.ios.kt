package com.example.rma1.di

import com.example.rma1.db.getDatabaseBuilder
import com.example.rma1.movies.db.AppDatabase
import com.example.rma1.movies.db.buildAppDatabase
import org.koin.dsl.module

actual fun databaseModule() = module {
    single<AppDatabase> {
        buildAppDatabase(builder = getDatabaseBuilder())
    }
}