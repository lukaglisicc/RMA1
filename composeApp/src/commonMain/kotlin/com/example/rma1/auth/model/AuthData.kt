package com.example.rma1.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String? = null,
    val username: String? = null,
    val realName: String? = null,
) {
    companion object {
        fun empty(): AuthData = AuthData(
            accessToken = "",
            username = "",
            realName = "",
        )
    }
}