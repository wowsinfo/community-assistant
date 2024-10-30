package com.half.wowsca.model.encyclopedia.items

import androidx.core.util.Pair
import com.half.wowsca.model.encyclopedia.CAItem
import org.json.JSONObject

/**
 * Created by slai4 on 4/25/2016.
 */
class ExteriorItem : CAItem() {
    @JvmField
    var description: String? = null
    @JvmField
    var type: String? = null
    @JvmField
    var coef: MutableMap<String?, Pair<String?, Float?>?>? = null


    override fun parse(`object`: JSONObject?) {
        if (`object` != null) {
            id = `object`.optLong("consumable_id")
            name = `object`.optString("name")
            description = `object`.optString("description")
            type = `object`.optString("type")
            image = `object`.optString("image")

            val coef = `object`.optJSONObject("profile")
            if (coef != null) {
                this.coef = HashMap()
                val iter = coef.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    val factor = coef.optJSONObject(key)
                    val amount = factor.optDouble("value")
                    val description = factor.optString("description")
                    (this.coef as HashMap<String?, Pair<String?, Float?>?>)[key] =
                        Pair(description, amount.toFloat())
                }
            }
        }
    }
}
