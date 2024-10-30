package com.half.wowsca.model

import org.json.JSONObject

/**
 * Created by slai4 on 5/1/2016.
 */
class CaptainPrivateInformation {
    @JvmField
    var gold: Int = 0
    @JvmField
    var freeExp: Int = 0
    @JvmField
    var credits: Double = 0.0
    @JvmField
    var premiumExpiresAt: Long = 0
    @JvmField
    var emptySlots: Int = 0
    @JvmField
    var slots: Int = 0
    @JvmField
    var battleTime: Long = 0

    fun parse(obj: JSONObject) {
        gold = obj.optInt("gold")
        freeExp = obj.optInt("free_exp")
        credits = obj.optDouble("credits")
        premiumExpiresAt = obj.optLong("premium_expires_at") * 1000
        emptySlots = obj.optInt("empty_slots")
        slots = obj.optInt("slots")
        battleTime = obj.optInt("battle_life_time").toLong()
    }
}
