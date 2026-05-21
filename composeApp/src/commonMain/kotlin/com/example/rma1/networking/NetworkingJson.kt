package com.example.rma1.networking

import kotlinx.serialization.json.Json

val NetworkingJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}
