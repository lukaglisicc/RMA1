package com.example.rma1.networking.auth

import com.example.rma1.auth.AuthManager
import com.example.rma1.auth.model.AuthState
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.SetupRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode

fun HttpClientConfig<*>.installAuthPlugin(
    authManager: AuthManager,
) = install(createClientPlugin("AuthPlugin") {

    on(SetupRequest) { request ->
        when (val authState = authManager.getAuthState()) {
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

            authManager.logOut()

            originalCall
        }
    }
})