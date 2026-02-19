package com.half.wowsca

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.enums.ShortcutRoutes
import com.half.wowsca.ui.SettingActivity
import com.utilities.logging.Dlog
import com.utilities.logging.Dlog.d
import com.utilities.preferences.Prefs
import org.greenrobot.eventbus.EventBus

/**
 * Created by slai4 on 9/15/2015.
 */
class CAApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //        AuthInfo.delete(getApplicationContext());
//        InfoManager.purge(getApplicationContext());
        // TODO: review what this does
//        DEVELOPMENT_MODE = !BuildConfig.BUILD_TYPE.equals("release");
        Dlog.LOGGING_MODE = DEVELOPMENT_MODE
        val pref = Prefs(applicationContext)
        var launchNumber = pref.getInt(LAUNCH_COUNT, 0)
        launchNumber++
        pref.setInt(LAUNCH_COUNT, launchNumber)
        val refreshedData = pref.getBoolean(FORCE_UPDATE_DATA, false)
        if (!refreshedData) {
            d("CAApp", "refreshing data")
            InfoManager.purge(applicationContext)
            pref.setBoolean(FORCE_UPDATE_DATA, true)
        }

        // this is a temp fix to remove ko from the settings page. This isn't always supported across servers
        if (getServerLanguage(applicationContext) == "ko") {
            setServerLanguage(applicationContext, getString(R.string.base_server_language))
        }
    }

    companion object {
        const val FORCE_UPDATE_DATA: String = "forceUpdateData5"
        const val APP_LANGUAGE: String = "appLanguage"
        const val PREF_FILE: String = "caapp"
        const val WOWS_API_SITE_ADDRESS: String = "https://api.worldofwarships"
        const val SELECTED_SERVER: String = "selected_server"
        const val SELECTED_SERVER_LANGUAGE: String = "selected_server_language"
        const val SELECTED_ID: String = "selectedId"
        const val COLORBLIND_MODE: String = "colorblindMode"
        const val LAUNCH_COUNT: String = "launchCount"
        var DEVELOPMENT_MODE: Boolean = false
        @JvmField
        var HAS_SHOWN_FIRST_DIALOG: Boolean = false
        @JvmField
        var ROUTING: ShortcutRoutes? = null

        /**
         * @param pos sending 0 clears out the previous number
         */
        @JvmField
        var lastShipPos: Int = 0
        @JvmStatic
        var infoManager: InfoManager? = null
            get() {
                if (field == null) {
                    field = InfoManager()
                }
                return field
            }
            private set

        @JvmStatic
        fun relaunchApplication(act: Activity) {
            act.finish()
            CompareManager.clear()
            val i = act.baseContext.packageManager
                .getLaunchIntentForPackage(act.baseContext.packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            act.startActivity(i)
        }

        @JvmStatic
        fun setTheme(act: FragmentActivity) {
            // For Android 12+ (API 31+), use dynamic colors with system theme detection
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Use system theme (light or dark) with dynamic colors
                val isSystemDark = (act.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
                
                if (isSystemDark) {
                    act.setTheme(R.style.Theme_CA_Material_Dark)
                } else {
                    act.setTheme(R.style.Theme_CA_Material3_White)
                }
            } else {
                // For older Android versions, use navy (ocean) theme as fallback
                act.setTheme(R.style.Theme_CA_Material_Ocean)
            }
        }

        @JvmStatic
        fun isOceanTheme(ctx: Context?): Boolean {
            // Ocean theme is only used on older Android versions
            return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S
        }

        @JvmStatic
        fun isDarkTheme(ctx: Context?): Boolean {
            if (ctx == null) return true
            // For Android 12+, check system theme
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                return (ctx.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
            // For older versions, navy theme is considered dark
            return true
        }

        @JvmStatic
        fun getTheme(ctx: Context?): String? {
            if (ctx == null) return "ocean"
            // For Android 12+, return based on system theme
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val isSystemDark = (ctx.resources.configuration.uiMode and 
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
                return if (isSystemDark) "dark" else "light"
            }
            // For older versions, always use ocean
            return "ocean"
        }

        @JvmStatic
        fun isNoArp(ctx: Context?): Boolean {
            val prefs = Prefs(ctx)
            return prefs.getBoolean(SettingActivity.NO_ARP, false)
        }

        @JvmStatic
        fun getTextColor(ctx: Context?): Int {
//        CAApp.isLightTheme(ctx) ? ContextCompat.getColor(ctx, R.color.material_text_primary_light) : for a different theme
            return ContextCompat.getColor(ctx!!, R.color.material_text_primary)
        }

        @JvmStatic
        val eventBus: EventBus
            get() = EventBus.getDefault()

        @JvmStatic
        fun getServerType(ctx: Context?): Server {
            val pref = Prefs(ctx)
            // have to handle removed region
            return try {
                Server.valueOf(pref.getString(SELECTED_SERVER, Server.NA.toString())!!)
            } catch (e: IllegalArgumentException) {
                Server.SEA
            }
        }

        @JvmStatic
        fun setServerType(ctx: Context?, s: Server) {
            val pref = Prefs(ctx)
            pref.setString(SELECTED_SERVER, s.toString())
        }

        @JvmStatic
        fun getServerLanguage(ctx: Context): String? {
            val pref = Prefs(ctx)
            val language = pref.getString(
                SELECTED_SERVER_LANGUAGE,
                ctx.getString(R.string.base_server_language)
            )
            return language
        }

        @JvmStatic
        fun setServerLanguage(ctx: Context?, language: String?) {
            val pref = Prefs(ctx)
            pref.setString(SELECTED_SERVER_LANGUAGE, language)
        }

        @JvmStatic
        fun getAppLanguage(ctx: Context): String? {
            val pref = Prefs(ctx)
            val language = pref.getString(APP_LANGUAGE, ctx.getString(R.string.app_langauge_base))
            return language
        }

        @JvmStatic
        fun setAppLanguage(ctx: Context?, language: String?) {
            val pref = Prefs(ctx)
            pref.setString(APP_LANGUAGE, language)
        }

        @JvmStatic
        fun getSelectedId(ctx: Context?): String? {
            val pref = Prefs(ctx)
            val selectedId = pref.getString(SELECTED_ID, null)
            return selectedId
        }

        @JvmStatic
        fun setSelectedId(ctx: Context?, id: String?) {
            val pref = Prefs(ctx)
            if (id != null) pref.setString(SELECTED_ID, id)
            else pref.remove(SELECTED_ID)
        }

        @JvmStatic
        fun isColorblind(ctx: Context?): Boolean {
            val pref = Prefs(ctx)
            return pref.getBoolean(COLORBLIND_MODE, false)
        }

        @JvmStatic
        fun setColorblindMode(ctx: Context?, mode: Boolean) {
            val pref = Prefs(ctx)
            pref.setBoolean(COLORBLIND_MODE, mode)
        }
    }
}
