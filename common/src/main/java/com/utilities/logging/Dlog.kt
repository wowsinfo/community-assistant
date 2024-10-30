package com.utilities.logging

import android.util.Log

object Dlog {
    @JvmField
    var LOGGING_MODE: Boolean = false

    @JvmStatic
    fun d(type: String?, message: String?) {
        if (LOGGING_MODE) try {
            Log.d(type, message!!)
        } catch (e: Exception) {
        }
    }

    fun e(type: String?, message: String?) {
        if (LOGGING_MODE) try {
            Log.e(type, message!!)
        } catch (e: Exception) {
        }
    }

    @JvmStatic
    fun wtf(type: String?, message: String?) {
        if (LOGGING_MODE) try {
            Log.wtf(type, message)
        } catch (e: Exception) {
        }
    }
}
