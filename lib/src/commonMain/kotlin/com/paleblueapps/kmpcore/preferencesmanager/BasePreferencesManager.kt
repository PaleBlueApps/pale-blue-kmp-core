package com.paleblueapps.kmpcore.preferencesmanager

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer

interface BasePreferencesManager {
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
    suspend fun clear()
}

interface PreferencesManager: BasePreferencesManager {
    fun getStringAsFlow(key: String): Flow<String?>
    fun getIntAsFlow(key: String): Flow<Int?>
    fun getLongAsFlow(key: String): Flow<Long?>
    fun getFloatAsFlow(key: String): Flow<Float?>
    fun getDoubleAsFlow(key: String): Flow<Double?>
    fun getBooleanAsFlow(key: String): Flow<Boolean?>
    fun <T> getObjectAsFlow(deserializer: DeserializationStrategy<T>, key: String): Flow<T?>
}

interface EncryptedPreferencesManager: BasePreferencesManager

suspend inline fun <reified T> BasePreferencesManager.putObject(key: String, value: T) {
    putObject(serializer(), key, value)
}

suspend inline fun <reified T> BasePreferencesManager.getObject(key: String): T? {
    return getObject(serializer(), key)
}

inline fun <reified T> PreferencesManager.getObjectAsFlow(key: String): Flow<T?> {
    return getObjectAsFlow(serializer(), key)
}
