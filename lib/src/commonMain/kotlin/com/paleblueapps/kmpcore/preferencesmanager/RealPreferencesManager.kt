package com.paleblueapps.kmpcore.preferencesmanager

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
internal class RealPreferencesManager(
    private val settings: FlowSettings,
    private val json: Json
) : PreferencesManager {

    override suspend fun getBoolean(key: String): Boolean? {
        return settings.getBooleanOrNull(key)
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    override suspend fun removeBoolean(key: String) {
        settings.remove(key)
    }

    override suspend fun getInt(key: String): Int? {
        return settings.getIntOrNull(key)
    }

    override suspend fun putInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    override suspend fun removeInt(key: String) {
        settings.remove(key)
    }

    override suspend fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    override suspend fun putString(key: String, value: String) {
        settings.putString(key, value)
    }

    override suspend fun removeString(key: String) {
        settings.remove(key)
    }

    override suspend fun getLong(key: String): Long? {
        return settings.getLongOrNull(key)
    }

    override suspend fun putLong(key: String, value: Long) {
        settings.putLong(key, value)
    }

    override suspend fun removeLong(key: String) {
        settings.remove(key)
    }

    override suspend fun getFloat(key: String): Float? {
        return settings.getFloatOrNull(key)
    }

    override suspend fun putFloat(key: String, value: Float) {
        settings.putFloat(key, value)
    }

    override suspend fun removeFloat(key: String) {
        settings.remove(key)
    }

    override suspend fun getDouble(key: String): Double? {
        return settings.getDoubleOrNull(key)
    }

    override suspend fun putDouble(key: String, value: Double) {
        settings.putDouble(key, value)
    }

    override suspend fun removeDouble(key: String) {
        settings.remove(key)
    }

    override suspend fun <T> getObject(deserializer: DeserializationStrategy<T>, key: String): T? {
        val raw = getString(key)
        return raw?.let { json.decodeFromString(deserializer, it) }
    }

    override suspend fun <T> putObject(
        serializer: SerializationStrategy<T>,
        key: String,
        value: T,
    ) {
        val raw = json.encodeToString(serializer, value)
        putString(key, raw)
    }

    override suspend fun removeObject(key: String) {
        settings.remove(key)
    }

    override fun getStringAsFlow(key: String): Flow<String?> {
        return settings.getStringOrNullFlow(key)
    }

    override fun getBooleanAsFlow(key: String): Flow<Boolean?> {
        return settings.getBooleanOrNullFlow(key)
    }

    override fun getDoubleAsFlow(key: String): Flow<Double?> {
        return settings.getDoubleOrNullFlow(key)
    }

    override fun getFloatAsFlow(key: String): Flow<Float?> {
        return settings.getFloatOrNullFlow(key)
    }

    override fun getIntAsFlow(key: String): Flow<Int?> {
        return settings.getIntOrNullFlow(key)
    }

    override fun getLongAsFlow(key: String): Flow<Long?> {
        return settings.getLongOrNullFlow(key)
    }

    override fun <T> getObjectAsFlow(
        deserializer: DeserializationStrategy<T>,
        key: String,
    ): Flow<T?> {
        return getStringAsFlow(key).map { it?.let { json.decodeFromString(deserializer, it) } }
    }

    override suspend fun clear() {
        settings.clear()
    }
}
