package com.example.rma1.di

import com.example.rma1.filters.FiltersViewModel
import com.example.rma1.main.MainViewModel
import com.example.rma1.movieDetails.MovieDetailsViewModel
import com.example.rma1.movies.MovieRepository
import com.example.rma1.movies.NetworkMovieRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    single { NetworkMovieRepository() } bind MovieRepository::class
    viewModelOf(::MainViewModel)
    viewModelOf(::MovieDetailsViewModel)
    viewModelOf(::FiltersViewModel)
}

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(
            mainModule,
        )
    }
}
