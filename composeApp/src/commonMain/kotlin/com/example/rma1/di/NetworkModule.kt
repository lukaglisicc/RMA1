package com.example.rma1.di

import com.example.rma1.movies.network.NetworkMovieApi
import com.example.rma1.movies.network.Qualifiers
import com.example.rma1.movies.network.createNetworkMovieApi
import com.example.rma1.networking.HttpClientFactory
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {

    single<HttpClient>(Qualifiers.Unauthenticated) {
        HttpClientFactory.createHttpClientWithDefaultConfig()
    }

    single<NetworkMovieApi> {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(Qualifiers.Unauthenticated))
            .baseUrl("https://rma.finlab.rs/")
            .build()
            .createNetworkMovieApi()
    }
}