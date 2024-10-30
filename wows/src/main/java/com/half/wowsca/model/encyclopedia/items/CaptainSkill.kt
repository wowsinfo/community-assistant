package com.half.wowsca.model.encyclopedia.items

import com.half.wowsca.model.encyclopedia.CAItem
import org.json.JSONObject

/**
 * Created by slai4 on 4/25/2016.
 */
class CaptainSkill : CAItem() {
    @JvmField
    var tier: Int = 0
    private var abilities: MutableList<String?>? = null

    override fun parse(jsonObject: JSONObject?) {
        if (jsonObject != null) {
            tier = jsonObject.optInt("tier")
            name = jsonObject.optString("name")
            image = jsonObject.optString("icon")
            val perks = jsonObject.optJSONArray("perks")
            if (perks != null) {
                setAbilities(ArrayList())
                for (i in 0 until perks.length()) {
                    val item = perks.optJSONObject(i)
                    abilities!!.add(item.optString("description"))
                }
            }
        }
    }

    fun getAbilities(): List<String?>? {
        return abilities
    }

    fun setAbilities(abilities: MutableList<String?>?) {
        this.abilities = abilities
    }
}
