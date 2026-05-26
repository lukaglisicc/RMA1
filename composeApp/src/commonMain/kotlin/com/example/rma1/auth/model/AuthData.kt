package com.example.rma1.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String? = null,
) {
    companion object {
        fun empty(): AuthData = AuthData(
            accessToken = "",
        )
    }
}