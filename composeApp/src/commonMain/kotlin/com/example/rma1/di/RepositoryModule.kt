package com.example.rma1.di

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.db.DatabaseMovieRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { DatabaseMovieRepository(appDatabase = get(), movieApi = get()) } bind MovieRepository::class
}