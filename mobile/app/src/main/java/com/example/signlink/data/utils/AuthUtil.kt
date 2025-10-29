package com.example.signlink.data.utils

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

object AuthUtil {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    val ROLE_KEY = stringPreferencesKey("user_role")

    fun basicAuth(): String {
        val credentials = "admin:admin"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun saveRole(context: Context, role: String) {
        context.dataStore.edit { prefs ->
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }.first()
    }

    suspend fun getRole(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[ROLE_KEY]
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { it.clear() }
    }

    suspend fun jwtAuth(context: Context): String? {
        val token = getToken(context) ?: return null
        return "Bearer $token"
    }
}
