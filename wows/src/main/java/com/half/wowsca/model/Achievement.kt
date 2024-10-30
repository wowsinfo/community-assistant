package com.half.wowsca.model

/**
 * Created by slai4 on 9/22/2015.
 */
class Achievement {
    @JvmField
    var name: String? = null
    @JvmField
    var number: Int = 0

    override fun toString(): String {
        return "Ach{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}'
    }
}
