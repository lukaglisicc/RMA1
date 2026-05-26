package com.example.rma1.networking.auth

import com.example.rma1.movies.network.LogInInfo
import com.example.rma1.movies.network.SignUpInfo
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface NetworkAuthApi {

    @POST("/auth/signup")
    suspend fun signUp(
        @Body signUpInfo: SignUpInfo,
    ): AuthToken

    @POST("/auth/login")
    suspend fun logIn(
        @Body logInInfo: LogInInfo,
    ): AuthToken
}

