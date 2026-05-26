package com.example.rma1.di

import com.example.rma1.auth.AuthStore
import com.example.rma1.auth.model.AuthState
import com.example.rma1.movies.network.NetworkMovieApi
import com.example.rma1.networking.Qualifiers
import com.example.rma1.movies.network.createNetworkMovieApi
import com.example.rma1.networking.HttpClientFactory
import com.example.rma1.networking.auth.NetworkAuthApi
import com.example.rma1.networking.auth.createNetworkAuthApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.SetupRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val networkModule = module {

    single<HttpClient>(Qualifiers.Unauthenticated) {
        HttpClientFactory.createHttpClientWithDefaultConfig()
    }

    single<HttpClient>(Qualifiers.Authenticated) {
        val authStoreLazy: Lazy<AuthStore> = inject()
        HttpClientFactory.createHttpClientWithDefaultConfig {
            installAuthPlugin(authStoreLazy)
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



private fun HttpClientConfig<*>.installAuthPlugin(
    authStoreLazy: Lazy<AuthStore>,
) = install(createClientPlugin("AuthPlugin") {

    on(SetupRequest) { request ->
        val authStore = authStoreLazy.value
        when (val authState = authStore.authState.value) {
            is AuthState.Authenticated -> {
                request.header(
                    key = HttpHeaders.Authorization,
                    value = "Bearer ${authState.data.accessToken}",
                )
            }
            AuthState.Unauthenticated -> Unit
        }
    }

    on(Send) { request ->
        val originalCall = proceed(request)

        originalCall.response.run {
            if (status != HttpStatusCode.Unauthorized) {
                return@run originalCall
            }

            // Token expired
            // Trebalo bi da osvezimo access token sa refresh tokenom
            // ali ovo nece biti potrebno za drugi projekat.
            // Od prilike bi ovako izgledalo, kada bismo radili:



            val newAuthState = runBlocking {
                // Zovemo refresh funkciju i vracamo njen rezultat
//                val authStore = authStoreLazy.value
//                authStore.refresh()

                // U ovom primeru pretvaramo se da je refresh token istekao
                AuthState.Unauthenticated
            }

            @Suppress("IMPOSSIBLE_IS_CHECK_WARNING")
            when (newAuthState) {
                is AuthState.Authenticated -> {
                    // Refresh uspeo - ponavljamo zahtev sa novim tokenom
                    request.headers.remove(name = HttpHeaders.Authorization)
                    request.headers.append(
                        name = HttpHeaders.Authorization,
                        value = "Bearer ${newAuthState.data.accessToken}",
                    )
                    proceed(request)
                }
                AuthState.Unauthenticated -> {
                    // Refresh puko - vracamo originalni 401 response
                    originalCall
                }
            }
        }
    }
})