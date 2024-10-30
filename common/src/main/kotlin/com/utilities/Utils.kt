package com.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import com.utilities.vaults.StringVault
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import java.net.URL
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

object Utils {
    fun interpolateValue(vO: Float, vT: Float, i: Float): Float {
        return ((vT - vO) * i) + vO
    }

    @JvmStatic
    @Throws(Exception::class)
    fun getInputStreamResponse(requested: URL): String {
        val client = OkHttpClient()
        val request: Request = Builder().url(requested).build()
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    fun validateUrl(url: String): String {
        return url.replace(" ".toRegex(), "%20").replace(",".toRegex(), "%2C")
    }

    @JvmStatic
    fun hasInternetConnection(ctx: Context): Boolean {
        val connectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun getHourMinuteFormatter(ctx: Context?): DateFormat {
        return if (android.text.format.DateFormat.is24HourFormat(ctx)) {
            // 24 hour format
            get24HourMinuteFormatter()
        } else {
            // 12 hour format
            hourMinuteFormatter
        }
    }

    @JvmStatic
    val defaultDecimalFormatter: DecimalFormat
        /**
         * #.##
         *
         * @return
         */
        get() = DecimalFormat(StringVault.DECIMAL_TWO_DEPTH)

    @JvmStatic
    val oneDepthDecimalFormatter: DecimalFormat
        /**
         * #.#
         *
         * @return
         */
        get() = DecimalFormat(StringVault.DECIMAL_ONE_DEPTH)


    val hourMinuteSecondFormatter: DateFormat
        /**
         * HH MM SS A
         *
         * @return
         */
        get() = SimpleDateFormat(StringVault.H_MM_SS_A)

    val speedDecimalFormatter: DecimalFormat
        /**
         * #.#
         *
         * @return
         */
        get() = DecimalFormat(StringVault.SPEED_DECIMAL)

    private val hourMinuteFormatter: DateFormat
        /**
         * H MM A
         *
         * @return
         */
        get() = SimpleDateFormat(StringVault.H_MM_A)

    /**
     * h MM A
     *
     * @return
     */
    private fun get24HourMinuteFormatter(): DateFormat {
        return SimpleDateFormat(StringVault.TWENTYFOUR_H_MM_A)
    }

    /**
     * MM DD YYYY
     *
     * @return
     */
    @JvmStatic
    fun getDayMonthYearFormatter(ctx: Context): DateFormat {
        val format = Settings.System.getString(ctx.contentResolver, Settings.System.DATE_FORMAT)
        return if (TextUtils.isEmpty(format)) {
            SimpleDateFormat(StringVault.MM_DD_YYYY)
        } else {
            SimpleDateFormat(format)
        }
    }

    val dayMonthFormatter: DateFormat
        /**
         * MM DD
         *
         * @return
         */
        get() = SimpleDateFormat(StringVault.MM_DD)

    val monthDayFormatter: DateFormat
        /**
         * MM DD
         *
         * @return
         */
        get() = SimpleDateFormat(StringVault.DD_MM)


    fun canUseExternalStorage(): Boolean {
        var mExternalStorageAvailable = false
        var mExternalStorageWriteable = false
        val state = Environment.getExternalStorageState()

        if (Environment.MEDIA_MOUNTED == state) {
            // We can read and write the media
            mExternalStorageWriteable = true
            mExternalStorageAvailable = mExternalStorageWriteable
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY == state) {
            // We can only read the media
            mExternalStorageAvailable = true
            mExternalStorageWriteable = false
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageWriteable = false
            mExternalStorageAvailable = mExternalStorageWriteable
        }
        return mExternalStorageWriteable
    }

    fun convertToMinutes(time: Long): Long {
        return (time / 1000) / 60
    }

    fun convertToHours(time: Long): Long {
        return convertToMinutes(time) / 60
    }

    fun convertToDays(time: Long): Long {
        return convertToHours(time) / 24
    }
}
