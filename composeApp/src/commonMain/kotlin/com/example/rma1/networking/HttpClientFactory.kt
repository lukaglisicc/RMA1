package com.example.rma1.networking

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json

object HttpClientFactory {


    fun createHttpClientWithDefaultConfig(
        block: HttpClientConfig<*>.() -> Unit = {}
    ): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(json = NetworkingJson)
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        throw ResponseException(
                            response = response,
                            cachedResponseText = "HTTP status is not successful: ${response.status}",
                        )
                    }
                }
            }

            install(UserAgent) {
                agent = UserAgentProvider.USER_AGENT
            }


            block.invoke(this)
        }
    }
}
