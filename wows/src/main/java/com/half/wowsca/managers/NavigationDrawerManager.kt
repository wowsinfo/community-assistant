package com.half.wowsca.managers

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.half.wowsca.CAApp.Companion.getAppLanguage
import com.half.wowsca.CAApp.Companion.getSelectedId
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.R
import com.half.wowsca.managers.CaptainManager.createCapIdStr
import com.half.wowsca.managers.CaptainManager.getCaptains
import com.half.wowsca.model.Captain
import com.half.wowsca.model.drawer.DrawerChild
import com.half.wowsca.model.enums.DrawerType
import com.half.wowsca.model.enums.Server
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import java.util.Collections
import java.util.Locale

/**
 * Created by slai4 on 11/29/2015.
 */
object NavigationDrawerManager {
    const val SEARCH: Int = 100
    const val CAPTAINS: Int = 700
    private const val DONATE_ID = 800
    var SHIPOPEDIA: Int = 200
    var WEBSITES_TOOLS: Int = 300
    var TWITCH: Int = 400
    var YOUTUBERS: Int = 500
    var SETTINGS: Int = 600
    var SERVER: Int = 900


    @JvmStatic
    fun createDrawer(
        act: AppCompatActivity,
        bar: Toolbar?,
        listener: Drawer.OnDrawerItemClickListener?
    ): Drawer {
        val headerResult = AccountHeaderBuilder()
            .withActivity(act)
            .withHeaderBackground(R.drawable.launcher_icon)
            .build()

        val result = DrawerBuilder()
            .withActivity(act)
            .withToolbar(bar!!)
            .withAccountHeader(headerResult)
            .addDrawerItems(
                *getDrawerItemList(act)
            )
            .withOnDrawerItemClickListener(listener!!)
            .withTranslucentStatusBar(false)
            .withSelectedItem(-1)
            .build()
        return result
    }

    private fun createPrimary(
        name: String,
        identifier: Int,
        type: DrawerType,
        icon: Int,
        isGroup: Boolean,
        isDarkTheme: Boolean
    ): PrimaryDrawerItem {
        val item = PrimaryDrawerItem()
        item.withName(name)
        item.withIdentifier(identifier.toLong())
        if (!isGroup) {
            val child = DrawerChild()
            child.title = name
            child.type = type
            item.withTag(child)
        }
        if (icon > 0) {
            item.withIcon(icon)
            if (!isDarkTheme) {
                item.withIconColorRes(R.color.material_primary)
                item.withIconTintingEnabled(true)
            }
        }
        return item
    }

    private fun createSecondary(
        name: String,
        id: Long,
        server: Server,
        type: DrawerType,
        icon: Int,
        isDarkTheme: Boolean
    ): SecondaryDrawerItem {
        val item = SecondaryDrawerItem()
        item.withName(name)

        val child = DrawerChild()
        child.title = name
        child.type = type
        child.id = id
        child.server = server
        item.withTag(child)

        if (icon > 0) {
            item.withIcon(icon)
            if (!isDarkTheme) {
                item.withIconColorRes(R.color.material_primary)
                item.withIconTintingEnabled(true)
            }
        }
        return item
    }

    @JvmStatic
    fun getDrawerItemList(ctx: Context): Array<IDrawerItem<*, *>> {
        val isDarkTheme = isDarkTheme(ctx)

        val current = getAppLanguage(ctx)
        val myLocale = Locale(current)
        val config = Configuration()
        config.locale = myLocale
        ctx.resources.updateConfiguration(
            config,
            ctx.resources.displayMetrics
        )

        val items: MutableList<IDrawerItem<*, *>> = ArrayList()

        // add search
        items.add(
            createPrimary(
                ctx.getString(R.string.drawer_search),
                SEARCH,
                DrawerType.SEARCH,
                R.drawable.ic_search,
                false,
                isDarkTheme
            )
        )

        // add other players
        items.add(
            createPrimary(
                ctx.getString(R.string.drawer_captains),
                0,
                DrawerType.SEARCH,
                R.drawable.ic_captains,
                true,
                isDarkTheme
            )
        )
        val favorite = getSelectedId(ctx)
        val captains: Map<String?, Captain?>? = getCaptains(ctx)
        val caps = captains!!.values
        val listCaps: List<Captain?> = ArrayList(caps)
        Collections.sort(listCaps) { lhs, rhs -> lhs!!.name.compareTo(rhs!!.name, ignoreCase = true) }
        for (captain in listCaps) {
            if (createCapIdStr(captain!!.server, captain.id) != favorite) {
                items.add(
                    createSecondary(
                        captain.name,
                        captain.id,
                        captain.server,
                        DrawerType.CAPTAIN,
                        0,
                        isDarkTheme
                    )
                )
            }
        }
        items.add(DividerDrawerItem())

        // add shipopedia
        items.add(
            createPrimary(
                ctx.getString(R.string.action_shipopedia),
                SHIPOPEDIA,
                DrawerType.SHIPOPEDIA,
                0,
                false,
                isDarkTheme
            )
        )

        // add websites and tools activity
        items.add(
            createPrimary(
                ctx.getString(R.string.action_websites),
                WEBSITES_TOOLS,
                DrawerType.WEBSITES_TOOLS,
                0,
                false,
                isDarkTheme
            )
        )

        // add twitch channels activity
        items.add(
            createPrimary(
                ctx.getString(R.string.action_twitch),
                TWITCH,
                DrawerType.TWITCH,
                0,
                false,
                isDarkTheme
            )
        )


        items.add(
            createPrimary(
                ctx.getString(R.string.action_donate),
                DONATE_ID,
                DrawerType.DONATE,
                0,
                false,
                isDarkTheme
            )
        )

        items.add(
            createPrimary(
                ctx.getString(R.string.action_server_info),
                SERVER,
                DrawerType.SERVER,
                0,
                false,
                isDarkTheme
            )
        )
        // add settings
        items.add(
            createPrimary(
                ctx.getString(R.string.action_settings),
                SETTINGS,
                DrawerType.SETTINGS,
                0,
                false,
                isDarkTheme
            )
        )

        val array: MutableList<IDrawerItem<*, *>> = mutableListOf()
        for (item in items) {
            array.add(item)
        }

        return array.toTypedArray()
    }
}
