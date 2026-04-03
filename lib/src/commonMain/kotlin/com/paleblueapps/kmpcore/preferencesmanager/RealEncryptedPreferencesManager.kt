package com.paleblueapps.kmpcore.preferencesmanager

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class RealEncryptedPreferencesManager(
    private val encryptedSettings: Settings,
    private val json: Json
) : EncryptedPreferencesManager {
    override suspend fun getBoolean(key: String): Boolean? {
        return encryptedSettings.getBooleanOrNull(key)
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        encryptedSettings.putBoolean(key, value)
    }

    override suspend fun removeBoolean(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun getInt(key: String): Int? {
        return encryptedSettings.getIntOrNull(key)
    }

    override suspend fun putInt(key: String, value: Int) {
        encryptedSettings.putInt(key, value)
    }

    override suspend fun removeInt(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun getString(key: String): String? {
        return encryptedSettings.getStringOrNull(key)
    }

    override suspend fun putString(key: String, value: String) {
        encryptedSettings.putString(key, value)
    }

    override suspend fun removeString(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun getLong(key: String): Long? {
        return encryptedSettings.getLongOrNull(key)
    }

    override suspend fun putLong(key: String, value: Long) {
        encryptedSettings.putLong(key, value)
    }

    override suspend fun removeLong(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun getFloat(key: String): Float? {
        return encryptedSettings.getFloatOrNull(key)
    }

    override suspend fun putFloat(key: String, value: Float) {
        encryptedSettings.putFloat(key, value)
    }

    override suspend fun removeFloat(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun getDouble(key: String): Double? {
        return encryptedSettings.getDoubleOrNull(key)
    }

    override suspend fun putDouble(key: String, value: Double) {
        encryptedSettings.putDouble(key, value)
    }

    override suspend fun removeDouble(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun <T> putObject(
        serializer: SerializationStrategy<T>,
        key: String,
        value: T
    ) {
        val raw = json.encodeToString(serializer, value)
        putString(key, raw)
    }

    override suspend fun <T> getObject(
        deserializer: DeserializationStrategy<T>,
        key: String
    ): T? {
        val raw = getString(key)
        return raw?.let { json.decodeFromString(deserializer, it) }
    }

    override suspend fun removeObject(key: String) {
        removeString(key)
    }

    override suspend fun clear() {
        encryptedSettings.clear()
    }
}