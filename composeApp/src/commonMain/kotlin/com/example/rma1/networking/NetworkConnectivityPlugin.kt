package com.example.rma1.networking
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import okio.IOException

fun HttpClientConfig<*>.installNetworkConnectivityPlugin(
    connectivityState: ConnectivityState,
) = install(createClientPlugin("NetworkConnectivityPlugin") {

    on(Send) { request ->
        try{
            val response = proceed(request)

            connectivityState.setOffline(false)

            response
        } catch (e: IOException) {

            connectivityState.setOffline(true)

            throw e
        }
    }
})