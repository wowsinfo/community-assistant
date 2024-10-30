package com.half.wowsca.alerts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.half.wowsca.R

/**
 * Created by Obsidian47 on 3/7/14.
 */
object Alert {
    @JvmStatic
    fun createGeneralAlert(
        ctx: Context?,
        title: String?,
        message: String?,
        neutralText: String?
    ): AlertDialog {
        val builder = AlertDialog.Builder(
            ctx!!
        )
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setNegativeButton(neutralText) { dialogInterface, i -> dialogInterface.dismiss() }
        return builder.show()
    }

    @JvmStatic
    fun createGeneralAlert(
        ctx: Context?,
        title: String?,
        message: String?,
        neutralText: String?,
        drawable: Int
    ): AlertDialog {
        val builder = AlertDialog.Builder(
            ctx!!
        )
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(drawable)
        builder.setNegativeButton(neutralText) { dialogInterface, i -> dialogInterface.dismiss() }
        return builder.show()
    }

    @JvmStatic
    fun createGeneralAlert(
        ctx: Context?,
        title: String?,
        message: String?,
        neutralText: String?,
        drawable: Drawable?
    ): AlertDialog {
        val builder = AlertDialog.Builder(
            ctx!!
        )
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(drawable)
        builder.setNegativeButton(neutralText) { dialogInterface, i -> dialogInterface.dismiss() }
        return builder.show()
    }

    @JvmStatic
    fun generalNoInternetDialogAlert(
        ctx: Activity,
        title: String?,
        message: String?,
        neutralText: String?
    ): AlertDialog {
        val alt_bld = AlertDialog.Builder(ctx)
        alt_bld.setTitle(title)
        alt_bld.setMessage(message)
        alt_bld.setCancelable(true)
        alt_bld.setPositiveButton(neutralText) { dialog, which ->
            dialog.dismiss()
            val intent = Intent()
            intent.setAction(Settings.ACTION_SETTINGS)
            ctx.startActivity(intent)
        }
        return alt_bld.show()
    }
}
