package com.utilities.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Prefs(ctx: Context?) {
    private val settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    fun remove(prefsName: String?) {
        val edit = settings.edit()
        edit.remove(prefsName)
        edit.apply()
    }

    fun getBoolean(prefsName: String?, defaultValue: Boolean): Boolean {
        return settings.getBoolean(prefsName, defaultValue)
    }

    fun setBoolean(prefsName: String?, value: Boolean) {
        val edit = settings.edit()
        edit.putBoolean(prefsName, value)
        edit.apply()
    }

    fun getString(prefsName: String?, defaultValue: String?): String? {
        return settings.getString(prefsName, defaultValue)
    }

    fun setString(prefsName: String?, value: String?) {
        val edit = settings.edit()
        edit.putString(prefsName, value)
        edit.apply()
    }

    fun getInt(prefsName: String?, defaultValue: Int): Int {
        return settings.getInt(prefsName, defaultValue)
    }

    fun setInt(prefsName: String?, defaultValue: Int) {
        val edit = settings.edit()
        edit.putInt(prefsName, defaultValue)
        edit.apply()
    }

    fun getLong(prefName: String?, defaultValue: Long): Long {
        return settings.getLong(prefName, defaultValue)
    }

    fun setLong(prefName: String?, value: Long) {
        val edit = settings.edit()
        edit.putLong(prefName, value)
        edit.apply()
    }
}
