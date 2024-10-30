package com.half.wowsca.model.encyclopedia

import org.json.JSONObject

/**
 * Created by slai4 on 4/25/2016.
 */
abstract class CAItem {
    var id: Long = 0
    var name: String? = null
    var image: String? = null

    abstract fun parse(jsonObject: JSONObject?)
}
