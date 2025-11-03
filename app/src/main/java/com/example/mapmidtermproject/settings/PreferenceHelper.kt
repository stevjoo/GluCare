package com.example.mapmidtermproject.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val PREF_NAME = "user_prefs"
    private val KEY_USERNAME = "username"
    private val KEY_PHONE = "phone"

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveUsername(username: String) {
        pref.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String {
        return pref.getString(KEY_USERNAME, "Belum diatur") ?: "Belum diatur"
    }

    fun savePhone(phone: String) {
        pref.edit().putString(KEY_PHONE, phone).apply()
    }

    fun getPhone(): String {
        return pref.getString(KEY_PHONE, "Belum diatur") ?: "Belum diatur"
    }
}
