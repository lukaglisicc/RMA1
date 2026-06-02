package com.example.rma1.di

import com.example.rma1.movies.network.NetworkMovieApi
import com.example.rma1.networking.Qualifiers
import com.example.rma1.movies.network.createNetworkMovieApi
import com.example.rma1.networking.ConnectivityState
import com.example.rma1.networking.HttpClientFactory
import com.example.rma1.networking.auth.NetworkAuthApi
import com.example.rma1.networking.auth.createNetworkAuthApi
import com.example.rma1.networking.auth.installAuthPlugin
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {

    single<ConnectivityState> { ConnectivityState() }

    single<HttpClient>(Qualifiers.Unauthenticated) {
        HttpClientFactory.createHttpClientWithDefaultConfig(connectivityState = get())
    }

    single<HttpClient>(Qualifiers.Authenticated) {
        HttpClientFactory.createHttpClientWithDefaultConfig(connectivityState = get()) {
            installAuthPlugin(get())
        }
    }

    single<NetworkMovieApi> {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(Qualifiers.Authenticated))
            .baseUrl("https://rma.finlab.rs/")
            .build()
            .createNetworkMovieApi()
    }

    single<NetworkAuthApi> {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(Qualifiers.Unauthenticated))
            .baseUrl("https://rma.finlab.rs/")
            .build()
            .createNetworkAuthApi()
    }
}