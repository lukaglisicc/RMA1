package com.example.rma1

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform