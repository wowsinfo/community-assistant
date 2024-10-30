package com.half.wowsca.model.drawer

import com.half.wowsca.model.enums.DrawerType
import com.half.wowsca.model.enums.Server


/**
 * Created by Harrison on 8/4/2014.
 */
class DrawerChild {
    @JvmField
    var title: String? = null
    var id: Long = 0
    @JvmField
    var type: DrawerType? = null
    var isSearch: Boolean = false
    @JvmField
    var server: Server? = null
}
