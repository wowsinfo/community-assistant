package com.half.wowsca.model

import com.half.wowsca.model.enums.TwitchStatus

/**
 * Created by slai4 on 12/3/2015.
 */
class TwitchObj(@JvmField var name: String) {
    @JvmField
    var url: String = "http://www.twitch.tv/$name"

    var isLive: TwitchStatus = TwitchStatus.OFFLINE

    @JvmField
    var gamePlaying: String? = null
    @JvmField
    var thumbnail: String? = null
    @JvmField
    var logo: String? = null
    @JvmField
    var streamName: String? = null
}
