package com.half.wowsca.model.enums

/**
 * Created by slai4 on 9/15/2015.
 */
enum class Server(val suffix: String, val serverName: String, val warshipsToday: String) {
    NA(".com", "na", "na"),
    EU(".eu", "eu", "eu"),
    SEA(".asia", "asia", "asia");

    val appId: String = "2acfbd91939b7bd250094257551d1f28"
}
