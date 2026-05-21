package com.example.rma1.di

import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.network.NetworkMovieRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single { NetworkMovieRepository() } bind MovieRepository::class
}