package com.half.wowsca.model

import android.content.Context
import com.utilities.preferences.Prefs
import java.util.Calendar

/**
 * Created by slai4 on 5/1/2016.
 */
class AuthInfo {
    @JvmField
    var token: String? = null
    @JvmField
    var expires: Long = 0
    @JvmField
    var account_id: Long = 0
    @JvmField
    var username: String? = null
    @JvmField
    var isExpired: Boolean = false

    fun save(ctx: Context?) {
        setAuthInfo(ctx, token, expires, account_id, username)
    }

    override fun toString(): String {
        return "AuthInfo{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                ", account_id=" + account_id +
                ", username='" + username + '\'' +
                ", isExpired=" + isExpired +
                '}'
    }

    companion object {
        const val LOGIN_ACCOUNT_ID: String = "login_account_id"
        const val LOGIN_EXPIRES: String = "login_expires"
        const val LOGIN_TOKEN: String = "login_token"
        const val LOGIN_USERNAME: String = "login_username"
        @JvmStatic
        fun getAuthInfo(ctx: Context?): AuthInfo {
            val info = AuthInfo()
            val prefs = Prefs(ctx)
            info.account_id = prefs.getLong(LOGIN_ACCOUNT_ID, 0)
            info.expires = prefs.getLong(LOGIN_EXPIRES, 0)
            info.token = prefs.getString(LOGIN_TOKEN, "")
            info.username = prefs.getString(LOGIN_USERNAME, "")

            val calendar = Calendar.getInstance()
            info.isExpired = calendar.timeInMillis > info.expires

            return info
        }

        fun setAuthInfo(
            ctx: Context?,
            token: String?,
            expireTime: Long,
            account_id: Long,
            username: String?
        ) {
            val prefs = Prefs(ctx)
            prefs.setString(LOGIN_TOKEN, token)
            prefs.setLong(LOGIN_EXPIRES, expireTime * 1000)
            prefs.setLong(LOGIN_ACCOUNT_ID, account_id)
            prefs.setString(LOGIN_USERNAME, username)
        }

        fun delete(ctx: Context?) {
            val prefs = Prefs(ctx)
            prefs.remove(LOGIN_ACCOUNT_ID)
            prefs.remove(LOGIN_EXPIRES)
            prefs.remove(LOGIN_TOKEN)
            prefs.remove(LOGIN_USERNAME)
        }
    }
}
