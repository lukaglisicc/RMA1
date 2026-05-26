package com.example.rma1.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String? = null,
    val refreshToken: String? = null,
) {
    companion object {
        fun empty(): AuthData = AuthData(
            accessToken = "",
            refreshToken = "",
        )
    }
}