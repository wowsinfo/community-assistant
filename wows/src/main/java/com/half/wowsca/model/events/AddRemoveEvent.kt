package com.half.wowsca.model.events

import com.half.wowsca.model.Captain

/**
 * Created by slai4 on 9/19/2015.
 */
class AddRemoveEvent {
    var isRemove: Boolean = false
    @JvmField
    var captain: Captain? = null
}
