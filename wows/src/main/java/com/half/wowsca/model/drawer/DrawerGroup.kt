package com.half.wowsca.model.drawer

import com.half.wowsca.model.enums.DrawerType


/**
 * Created by Harrison on 8/4/2014.
 */
class DrawerGroup {
    var children: List<DrawerChild> = ArrayList()
    var type: DrawerType? = null
    var title: String? = null
}
