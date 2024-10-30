package com.half.wowsca.model.encyclopedia.items

import com.half.wowsca.model.encyclopedia.CAItem
import org.json.JSONObject

/**
 * Created by slai4 on 10/31/2015.
 */
class EquipmentInfo : CAItem() {
    var tag: String? = null
    @JvmField
    var price: Int = 0
    @JvmField
    var description: String? = null
    var type: String? = null

    override fun parse(jsonObject: JSONObject?) {
        if (jsonObject != null) {
            name = jsonObject.optString("name")
            price = jsonObject.optInt("price_credit")
            type = jsonObject.optString("type")
            image = jsonObject.optString("image")
            tag = jsonObject.optString("tag")
            type = jsonObject.optString("type")
            description = jsonObject.optString("description")
        }
    }
}
