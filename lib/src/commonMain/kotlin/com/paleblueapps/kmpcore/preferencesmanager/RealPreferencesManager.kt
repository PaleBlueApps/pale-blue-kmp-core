package com.paleblueapps.kmpcore.preferencesmanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

internal class RealPreferencesManager(
    private val datastore: DataStore<Preferences>,
    private val encryptedSettings: Settings,
) : PreferencesManager {

    override suspend fun getBoolean(key: String): Boolean? {
        val datastoreKey = booleanPreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val datastoreKey = booleanPreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeBoolean(key: String) {
        val datastoreKey = booleanPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getInt(key: String): Int? {
        val datastoreKey = intPreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]
    }

    override suspend fun putInt(key: String, value: Int) {
        val datastoreKey = intPreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeInt(key: String) {
        val datastoreKey = intPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getString(key: String): String? {
        val datastoreKey = stringPreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]
    }

    override suspend fun putString(key: String, value: String) {
        val datastoreKey = stringPreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeString(key: String) {
        val datastoreKey = stringPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getLong(key: String): Long? {
        val datastoreKey = longPreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]?.toLong()
    }

    override suspend fun putLong(key: String, value: Long) {
        val datastoreKey = longPreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeLong(key: String) {
        val datastoreKey = longPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getFloat(key: String): Float? {
        val datastoreKey = floatPreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]
    }

    override suspend fun putFloat(key: String, value: Float) {
        val datastoreKey = floatPreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeFloat(key: String) {
        val datastoreKey = floatPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getDouble(key: String): Double? {
        val datastoreKey = doublePreferencesKey(key)
        val preferences = datastore.data.first()
        return preferences[datastoreKey]?.toDouble()
    }

    override suspend fun putDouble(key: String, value: Double) {
        val datastoreKey = doublePreferencesKey(key)
        datastore.edit { preferences ->
            preferences[datastoreKey] = value
        }
    }

    override suspend fun removeDouble(key: String) {
        val datastoreKey = doublePreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun <T> getObject(deserializer: DeserializationStrategy<T>, key: String): T? {
        val raw = getString(key)
        return raw?.let { Json.decodeFromString(deserializer, it) }
    }

    override suspend fun <T> putObject(
        serializer: SerializationStrategy<T>,
        key: String,
        value: T,
    ) {
        val raw = Json.encodeToString(serializer, value)
        putString(key, raw)
    }

    override suspend fun removeObject(key: String) {
        val datastoreKey = stringPreferencesKey(key)
        datastore.edit { preferences ->
            preferences.remove(datastoreKey)
        }
    }

    override suspend fun getEncryptedString(key: String): String? {
        return encryptedSettings.getStringOrNull(key)
    }

    override suspend fun putEncryptedString(key: String, value: String) {
        encryptedSettings.putString(key, value)
    }

    override suspend fun removeEncryptedString(key: String) {
        encryptedSettings.remove(key)
    }

    override suspend fun <T> getEncryptedObject(
        deserializer: DeserializationStrategy<T>,
        key: String,
    ): T? {
        val raw = getEncryptedString(key)
        return raw?.let { Json.decodeFromString(deserializer, it) }
    }

    override suspend fun <T> putEncryptedObject(
        serializer: SerializationStrategy<T>,
        key: String,
        value: T,
    ) {
        val raw = Json.encodeToString(serializer, value)
        putEncryptedString(key, raw)
    }

    override fun getStringAsFlow(key: String): Flow<String?> {
        val datastoreKey = stringPreferencesKey(key)
        return datastore.data.map { preferences -> preferences[datastoreKey] }
    }

    override fun <T> getObjectAsFlow(
        deserializer: DeserializationStrategy<T>,
        key: String,
    ): Flow<T?> {
        val raw = getStringAsFlow(key)
        return raw.map { it?.let { Json.decodeFromString(deserializer, it) } }
    }

    override suspend fun clear() {
        datastore.edit { preferences ->
            preferences.clear()
        }
        encryptedSettings.clear()
    }
}
