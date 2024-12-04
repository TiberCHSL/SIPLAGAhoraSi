package com.example.siplagmovil.data.model.local

import android.content.Context
import android.content.SharedPreferences
import com.example.siplagmovil.utils.Constants

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // Save JWT token
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(Constants.JWT_KEY, token).apply()
    }

    // Get JWT token
    fun getToken(): String? {
        return sharedPreferences.getString(Constants.JWT_KEY, null)
    }

    // Clear JWT token
    fun clearToken() {
        sharedPreferences.edit().remove(Constants.JWT_KEY).apply()
    }
}