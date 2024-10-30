package com.half.wowsca.model

import com.half.wowsca.model.enums.Server

/**
 * Created by slai4 on 12/2/2015.
 */
class ServerInfo {
    @JvmField
    var server: Server? = null
    @JvmField
    var name: String? = null
    @JvmField
    var players: Int = 0
}
