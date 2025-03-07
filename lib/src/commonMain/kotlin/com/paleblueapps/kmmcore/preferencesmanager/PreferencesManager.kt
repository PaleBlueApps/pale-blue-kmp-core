package com.paleblueapps.kmmcore.preferencesmanager

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

interface PreferencesManager {
    suspend fun getBoolean(key: String): Boolean?
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun removeBoolean(key: String)

    suspend fun getInt(key: String): Int?
    suspend fun putInt(key: String, value: Int)
    suspend fun removeInt(key: String)

    suspend fun getString(key: String): String?
    suspend fun putString(key: String, value: String)
    suspend fun removeString(key: String)

    suspend fun getLong(key: String): Long?
    suspend fun putLong(key: String, value: Long)
    suspend fun removeLong(key: String)

    suspend fun getFloat(key: String): Float?
    suspend fun putFloat(key: String, value: Float)
    suspend fun removeFloat(key: String)

    suspend fun getDouble(key: String): Double?
    suspend fun putDouble(key: String, value: Double)
    suspend fun removeDouble(key: String)

    suspend fun <T> putObject(serializer: SerializationStrategy<T>, key: String, value: T)
    suspend fun <T> getObject(deserializer: DeserializationStrategy<T>, key: String): T?
    suspend fun removeObject(key: String)

    suspend fun getEncryptedString(key: String): String?
    suspend fun putEncryptedString(key: String, value: String)
    suspend fun removeEncryptedString(key: String)

    suspend fun <T> putEncryptedObject(serializer: SerializationStrategy<T>, key: String, value: T)
    suspend fun <T> getEncryptedObject(deserializer: DeserializationStrategy<T>, key: String): T?

    fun getStringAsFlow(key: String): Flow<String?>
    fun <T> getObjectAsFlow(deserializer: DeserializationStrategy<T>, key: String): Flow<T?>

    suspend fun clear()
}

suspend inline fun <reified T> PreferencesManager.putObject(key: String, value: T) {
    putObject(serializer(), key, value)
}

suspend inline fun <reified T> PreferencesManager.getObject(key: String): T? {
    return getObject(serializer(), key)
}

suspend inline fun <reified T> PreferencesManager.putEncryptedObject(key: String, value: T) {
    putEncryptedObject(serializer(), key, value)
}

suspend inline fun <reified T> PreferencesManager.getEncryptedObject(key: String): T? {
    return getEncryptedObject(serializer(), key)
}
