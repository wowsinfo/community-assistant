package com.half.wowsca.model

import org.json.JSONObject

/**
 * Created by slai4 on 9/19/2015.
 */
class BatteryStats {
    @JvmField
    var maxFrags: Int = 0
    @JvmField
    var frags: Int = 0
    @JvmField
    var hits: Int = 0
    @JvmField
    var shots: Int = 0
    @JvmField
    var maxFragsShipId: Long = 0

    override fun toString(): String {
        return "Battery{" +
                "maxFrags=" + maxFrags +
                ", frags=" + frags +
                ", hits=" + hits +
                ", shots=" + shots +
                ", maxFragsShipId=" + maxFragsShipId +
                '}'
    }

    companion object {
        @JvmStatic
        fun parse(json: JSONObject?): BatteryStats {
            val stats = BatteryStats()
            if (json != null) {
                stats.maxFrags = json.optInt("max_frags_battle")
                stats.frags = json.optInt("frags")
                stats.hits = json.optInt("hits")
                stats.maxFragsShipId = json.optLong("max_frags_ship_id")
                stats.shots = json.optInt("shots")
            }
            return stats
        }
    }
}
