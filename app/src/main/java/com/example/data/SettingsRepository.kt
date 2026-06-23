package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val appLanguageKey = stringPreferencesKey("app_language")
    // Use true for Dark Mode, false for Light Mode, null for System Default
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val ttsEnabledKey = booleanPreferencesKey("tts_enabled")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    
    private val userNameKey = stringPreferencesKey("user_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val dietaryPreferencesKey = stringPreferencesKey("dietary_prefs") // Used as comma separated string

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[appLanguageKey] ?: "en" // Default is English
    }

    val darkModeFlow: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[darkModeKey]
    }
    
    val ttsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ttsEnabledKey] ?: true // Default enabled
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[userNameKey] ?: ""
    }

    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[userEmailKey] ?: ""
    }

    val dietaryPrefsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[dietaryPreferencesKey]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun setLanguage(langCode: String) {
        context.dataStore.edit { prefs -> prefs[appLanguageKey] = langCode }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[darkModeKey] = enabled }
    }
    
    suspend fun setTtsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[ttsEnabledKey] = enabled }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[userNameKey] = name }
    }

    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { prefs -> prefs[userEmailKey] = email }
    }

    suspend fun setDietaryPrefs(prefs: List<String>) {
        context.dataStore.edit { preferences -> preferences[dietaryPreferencesKey] = prefs.joinToString(",") }
    }
}
