package com.half.wowsca.model.enums

/**
 * Created by slai4 on 4/17/2016.
 */
enum class TwitchStatus(@JvmField var order: Int) {
    LIVE(-2),
    YOUTUBE(-1),
    OFFLINE(1)
}
