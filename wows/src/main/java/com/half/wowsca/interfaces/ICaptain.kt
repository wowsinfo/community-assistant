package com.half.wowsca.interfaces

import android.content.Context
import com.half.wowsca.model.Captain

/**
 * Created by slai4 on 9/19/2015.
 */
interface ICaptain {
    fun getCaptain(ctx: Context?): Captain?
}
