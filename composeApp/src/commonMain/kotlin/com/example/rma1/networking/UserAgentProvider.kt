package com.example.rma1.networking


object UserAgentProvider {
    val USER_AGENT: String = "RMA-App ${getUserAgentPlatformName()}"
}

expect fun getUserAgentPlatformName(): String
