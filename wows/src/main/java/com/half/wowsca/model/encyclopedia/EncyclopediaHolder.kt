package com.half.wowsca.model.encyclopedia

/**
 * Created by slai4 on 4/25/2016.
 */
open class EncyclopediaHolder<X, Y> {
    var items: MutableMap<X, Y>?

    init {
        items = HashMap()
    }

    operator fun get(x: X): Y? {
        return if (items != null) items!![x]
        else null
    }

    fun put(x: X, y: Y) {
        if (items != null) items!![x] = y
    }
}
