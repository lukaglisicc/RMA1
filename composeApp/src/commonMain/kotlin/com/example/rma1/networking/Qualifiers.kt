package com.example.rma1.networking

import org.koin.core.qualifier.named

object Qualifiers {
    val Authenticated = named("Authenticated")
    val Unauthenticated = named("Unauthenticated")
}