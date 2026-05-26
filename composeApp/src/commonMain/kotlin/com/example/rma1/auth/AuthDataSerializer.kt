package com.example.rma1.auth

import androidx.datastore.core.okio.OkioSerializer
import com.example.rma1.auth.model.AuthData
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource


object AuthDataSerializer : OkioSerializer<AuthData> {

    override val defaultValue: AuthData = AuthData.empty()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override suspend fun readFrom(source: BufferedSource): AuthData {
        return try {
            val jsonString = source.readUtf8()
            if (jsonString.isEmpty()) {
                return defaultValue
            }

            json.decodeFromString<AuthData>(jsonString)
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: AuthData, sink: BufferedSink) {
        val jsonString = json.encodeToString(
            serializer =  AuthData.serializer(),
            value = t,
        )
        sink.writeUtf8(jsonString)
    }
}
