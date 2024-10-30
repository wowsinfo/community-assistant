package com.half.wowsca.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.half.wowsca.CAApp
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.CAApp.Companion.isDarkTheme
import com.half.wowsca.CAApp.Companion.isNoArp
import com.half.wowsca.CAApp.Companion.setSelectedId
import com.half.wowsca.R
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Statistics
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.enums.Server
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.d
import com.utilities.preferences.Prefs
import java.text.DecimalFormat
import java.util.Locale

/**
 * Created by slai4 on 9/19/2015.
 */
object UIUtils {
    const val MYOKO_SHIP_ID: Long = 4286494416L
    const val KONGO_SHIP_ID: Long = 4287575760L
    private var arpShips: MutableMap<Long, Long>? = null

    @JvmStatic
    fun createReviewDialog(act: Activity) {
        val prefs = Prefs(act)
        val hasReviewed = prefs.getBoolean("hasReviewed", false)
        if (!hasReviewed) {
            val builder = AlertDialog.Builder(act)
            builder.setTitle(act.getString(R.string.dialog_review_app))
            builder.setMessage(act.getString(R.string.dialog_review_message))
            builder.setPositiveButton(act.getString(R.string.dialog_review_positive)) { dialog, which ->
                val prefs = Prefs(act)
                prefs.setBoolean("hasReviewed", true)
                val url = "https://play.google.com/store/apps/details?id=com.half.wowsca"
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                act.startActivity(i)
                dialog.dismiss()
            }
            builder.setNegativeButton(act.getString(R.string.no)) { dialog, which -> dialog.dismiss() }
            builder.show()
            CAApp.HAS_SHOWN_FIRST_DIALOG = true
        }
    }

    @JvmStatic
    fun createDonationDialog(act: Activity) {
        val prefs = Prefs(act)
        val hasDonated = prefs.getBoolean("hasDonated2", false)
        if (!hasDonated) {
            val builder = AlertDialog.Builder(act)
            builder.setTitle(act.getString(R.string.dialog_assist_title))
            builder.setMessage(act.getString(R.string.dialog_assist_message))
            builder.setPositiveButton(act.getString(R.string.patreon)) { dialog, which ->
                val prefs = Prefs(act)
                prefs.setBoolean("hasDonated2", true)
                val url = "https://patreon.com/slai47"
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                act.startActivity(i)
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.dismiss) { dialog, which -> dialog.dismiss() }
            builder.setNeutralButton(act.getString(R.string.dialog_view_ad)) { dialog, which ->
                val i = Intent(act.applicationContext, ResourcesActivity::class.java)
                i.putExtra(ResourcesActivity.EXTRA_TYPE, ResourcesActivity.EXTRA_DONATE)
                i.putExtra(ResourcesActivity.EXTRA_VIEW_AD, true)
                act.startActivity(i)
                dialog.dismiss()
            }
            builder.show()
            CAApp.HAS_SHOWN_FIRST_DIALOG = true
        }
    }

    @JvmStatic
    fun createFollowDialog(act: Activity) {
        val prefs = Prefs(act)
        val hasFollowed = prefs.getBoolean("hasFollowed", false)
        if (!hasFollowed) {
            val builder = AlertDialog.Builder(act)
            builder.setTitle(act.getString(R.string.dialog_follow_title))
            builder.setMessage(act.getString(R.string.dialog_follow_message))
            builder.setPositiveButton("Twitter") { dialog, which ->
                val prefs = Prefs(act)
                prefs.setBoolean("hasFollowed", true)
                val url = "https://twitter.com/slai47"
                val i = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                act.startActivity(i)
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.dismiss) { dialog, which -> dialog.dismiss() }
            builder.show()
            CAApp.HAS_SHOWN_FIRST_DIALOG = true
        }
    }

    @JvmStatic
    fun createBookmarkingDialogIfNeeded(act: Activity, p: Captain) {
        val prefs = Prefs(act)
        val isFirstTimeAddingPlayer = prefs.getBoolean("hasSeenSelectedDialog", true)
        if (isFirstTimeAddingPlayer) {
            val builder = AlertDialog.Builder(act)
            builder.setTitle(act.getString(R.string.dialog_bookmark_title))
            builder.setMessage(act.getString(R.string.dialog_bookmark_message))

            builder.setPositiveButton(act.getString(R.string.yes)) { dialog, which ->
                setSelectedId(act.applicationContext, CaptainManager.createCapIdStr(p.server, p.id))
                act.invalidateOptionsMenu()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.no) { dialog, which -> dialog.dismiss() }
            builder.show()
            prefs.setBoolean("hasSeenSelectedDialog", false)
        }
    }

    @JvmStatic
    fun createCaptainListViewMenu(act: Activity, currentId: String) {
        val captainsView = act.findViewById<View>(R.id.action_captains)
        val popupMenu = PopupMenu(act, captainsView)
        val inflate = popupMenu.menuInflater
        inflate.inflate(R.menu.menu_popup_captains, popupMenu.menu)
        val captains = CaptainManager.getCaptains(act.applicationContext)
        val caps: MutableCollection<Captain?> = captains?.values ?: ArrayList()
        for (captain in caps) {
            if (CaptainManager.createCapIdStr(
                    captain?.server,
                    captain?.id
                ) != currentId
            ) popupMenu.menu.add(
                captain?.server.toString().uppercase(
                    Locale.getDefault()
                ) + " " + captain?.name
            )
        }
        popupMenu.setOnMenuItemClickListener { item ->
            val name = item.title.toString()
            val split = name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val server = Server.valueOf(split[0])
            val captainName = split[1]
            val captains = CaptainManager.getCaptains(act.applicationContext)
            val caps: MutableCollection<Captain?> = captains?.values ?: ArrayList()
            for (captain in caps) {
                if (captain?.name == captainName && captain.server.ordinal == server.ordinal) {
                    val i = Intent(act.applicationContext, ViewCaptainActivity::class.java)
                    i.putExtra(ViewCaptainActivity.EXTRA_ID, captain.id)
                    i.putExtra(ViewCaptainActivity.EXTRA_SERVER, captain.server.toString())
                    i.putExtra(ViewCaptainActivity.EXTRA_NAME, captain.name)
                    act.startActivity(i)
                    break
                }
            }
            false
        }
        popupMenu.show()
    }

    @JvmStatic
    fun setUpCard(parent: View, id: Int) {
        var card: CardView? = null
        card = if (id > 0) parent.findViewById(id)
        else parent as CardView
        var backgroundInt = R.color.transparent
        if (isDarkTheme(parent.context)) backgroundInt = R.color.material_card_background_dark
        card!!.setCardBackgroundColor(ContextCompat.getColor(parent.context, backgroundInt))
        card.radius = 6.0f
        card.cardElevation = 4f
    }

    @JvmStatic
    fun getNationText(ctx: Context, nationCode: String): String {
        var nation = nationCode
        if (nation == "ussr") {
            nation = ctx.getString(R.string.russia)
        } else if (nation == "germany") {
            nation = ctx.getString(R.string.germany)
        } else if (nation == "usa") {
            nation = ctx.getString(R.string.usa)
        } else if (nation == "poland") {
            nation = ctx.getString(R.string.poland)
        } else if (nation == "japan") {
            nation = ctx.getString(R.string.japan)
        } else if (nation == "uk") {
            nation = ctx.getString(R.string.uk)
        } else if (nation == "pan_asia") {
            nation = ctx.getString(R.string.pan_asia)
        } else if (nation == "france") {
            nation = ctx.getString(R.string.nation_france)
        } else if (nation == "commonwealth") {
            nation = ctx.getString(R.string.nation_commonwealth)
        }
        return nation
    }

    @JvmStatic
    fun setShipImage(view: ImageView, ship: ShipInfo?) {
        setShipImage(view, ship, false)
    }

    @JvmStatic
    fun setShipImage(view: ImageView, ship: ShipInfo?, forceBigImage: Boolean) {
        var ship = ship
        if (arpShips == null) {
            arpShips = HashMap<Long, Long>()
            arpShips?.set(3551442640L, MYOKO_SHIP_ID) // haguro
            arpShips?.set(3552523984L, KONGO_SHIP_ID) // Hiei
            arpShips?.set(3553572560L, KONGO_SHIP_ID) // haruna
            arpShips?.set(3554621136L, KONGO_SHIP_ID) // kirishima
            arpShips?.set(3555636944L, MYOKO_SHIP_ID) // arp myoko
            arpShips?.set(3555669712L, KONGO_SHIP_ID) // arp Kongo
        }
        var highDefImage = view.context.resources.getBoolean(R.bool.high_def_images)
        if (forceBigImage) highDefImage = true
        if (isNoArp(view.context)) {
            val arpShip = arpShips!![ship!!.shipId]
            if (arpShip != null) {
                ship = infoManager!!.getShipInfo(view.context)[arpShip]
            }
        }
        if (ship != null) Picasso.get().load(if (highDefImage) ship.bestImage else ship.image)
            .error(R.drawable.ic_missing_image).into(view, object : Callback {
                override fun onSuccess() {
                }

                override fun onError(e: Exception) {
                    d(UIUtils::class.java.name, e.message)
                }
            })
    }

    @JvmStatic
    fun createOtherStatsArea(area: LinearLayout, listStrs: List<String?>, list: List<Statistics>) {
        area.removeAllViews()
        for (i in list.indices) {
            val title = listStrs[i]
            val stats = list[i]

            val view =
                LayoutInflater.from(area.context).inflate(R.layout.list_statistics, area, false)

            setUpCard(view, R.id.list_statistics_area)

            val tvTitle = view.findViewById<TextView>(R.id.list_statistics_title)

            val tvDamage = view.findViewById<TextView>(R.id.list_statistics_avg_dmg)
            val tvBattles = view.findViewById<TextView>(R.id.list_statistics_battles)
            val tvAvgExp = view.findViewById<TextView>(R.id.list_statistics_avg_exp)
            val tvWinRate = view.findViewById<TextView>(R.id.list_statistics_win_rate)
            val tvAvgKills = view.findViewById<TextView>(R.id.list_statistics_k_d)

            val tvBatteryMain = view.findViewById<TextView>(R.id.list_statistics_battery_kills_main)
            val tvBatteryTorps =
                view.findViewById<TextView>(R.id.list_statistics_battery_kills_torps)
            val tvBatteryAircraft =
                view.findViewById<TextView>(R.id.list_statistics_battery_kills_aircraft)
            val tvBatteryOther =
                view.findViewById<TextView>(R.id.list_statistics_battery_kills_other)


            val tvMaxKills = view.findViewById<TextView>(R.id.list_statistics_max_kills)
            val tvMaxDamage = view.findViewById<TextView>(R.id.list_statistics_max_dmg)
            val tvMaxPlanes = view.findViewById<TextView>(R.id.list_statistics_max_planes_killed)
            val tvMaxXP = view.findViewById<TextView>(R.id.list_statistics_max_xp)

            val tvTotalKills = view.findViewById<TextView>(R.id.list_statistics_kills)
            val tvTotalDamage = view.findViewById<TextView>(R.id.list_statistics_total_dmg)
            val tvTotalPlanes = view.findViewById<TextView>(R.id.list_statistics_planes_downed)
            val tvTotalXP = view.findViewById<TextView>(R.id.list_statistics_total_xp)


            val tvSurvivalRate = view.findViewById<TextView>(R.id.list_statistics_survival_rate)
            val tvSurvivedWins = view.findViewById<TextView>(R.id.list_statistics_survived_wins)
            val tvCaptures = view.findViewById<TextView>(R.id.list_statistics_captures)

            val tvMainAccuracy =
                view.findViewById<TextView>(R.id.list_statistics_main_battery_accuracy)
            val tvTorpAccuracy =
                view.findViewById<TextView>(R.id.list_statistics_torp_battery_accuracy)
            val tvDrpCaptures = view.findViewById<TextView>(R.id.list_statistics_drp_captures)


            val tvSpottingDamage = view.findViewById<TextView>(R.id.list_statistics_total_spotting)
            val tvArgoDamage = view.findViewById<TextView>(R.id.list_statistics_total_argo)
            val tvBuildingDamage = view.findViewById<TextView>(R.id.list_statistics_total_building)
            val tvArgoTorpDamage = view.findViewById<TextView>(R.id.list_statistics_total_torp_argo)

            val tvSuppressionCount =
                view.findViewById<TextView>(R.id.list_statistics_total_supressions)
            val tvSpottingCount = view.findViewById<TextView>(R.id.list_statistics_total_spots)
            val tvMaxSpotting = view.findViewById<TextView>(R.id.list_statistics_max_spots)

            tvTitle.text = title

            val battles = stats.battles.toFloat()

            tvDamage.text = (stats.totalDamage / battles).toInt().toString() + ""
            tvBattles.text = battles.toInt().toString() + ""
            tvAvgExp.text = (stats.totalXP / battles).toInt().toString() + ""
            tvWinRate.text =
                defaultDecimalFormatter.format(((stats.wins / battles) * 100).toDouble()) + "%"
            tvAvgKills.text =
                defaultDecimalFormatter.format((stats.frags / battles).toDouble())

            tvBatteryMain.text = stats.mainBattery.frags.toString() + ""
            tvBatteryTorps.text = stats.torpedoes.frags.toString() + ""
            tvBatteryAircraft.text = stats.aircraft.frags.toString() + ""
            val other =
                stats.frags - stats.mainBattery.frags - stats.torpedoes.frags - stats.aircraft.frags
            tvBatteryOther.text = other.toString() + ""

            val format = DecimalFormat("###,###,###")

            tvMaxKills.text = stats.maxFragsInBattle.toString() + ""
            tvMaxDamage.text = format.format(stats.maxDamage.toLong())
            tvMaxPlanes.text = stats.maxPlanesKilled.toString() + ""
            tvMaxXP.text = format.format(stats.maxXP)

            tvTotalKills.text = format.format(stats.frags.toLong())
            tvTotalDamage.text = format.format(stats.totalDamage)
            tvTotalPlanes.text = stats.planesKilled.toString() + ""
            tvTotalXP.text = format.format(stats.totalXP)

            tvSurvivalRate.text =
                oneDepthDecimalFormatter.format(((stats.survivedBattles / battles) * 100).toDouble()) + "%"
            tvSurvivedWins.text =
                oneDepthDecimalFormatter.format(((stats.survivedWins / battles) * 100).toDouble()) + "%"
            tvCaptures.text =
                oneDepthDecimalFormatter.format((stats.capturePoints / battles).toDouble())
            tvDrpCaptures.text =
                oneDepthDecimalFormatter.format((stats.droppedCapturePoints / battles).toDouble())

            tvMainAccuracy.text =
                oneDepthDecimalFormatter.format(((stats.mainBattery.hits / stats.mainBattery.shots.toFloat()) * 100).toDouble()) + "%"
            tvTorpAccuracy.text =
                oneDepthDecimalFormatter.format(((stats.torpedoes.hits / stats.torpedoes.shots.toFloat()) * 100).toDouble()) + "%"

            val ctx = area.context
            var argoDamage = "" + stats.totalArgoDamage
            if (stats.totalArgoDamage > 1000000) {
                argoDamage =
                    defaultDecimalFormatter.format(stats.totalArgoDamage / 1000000) + ctx.getString(
                        R.string.million
                    )
            }
            tvArgoDamage.text = argoDamage

            var argoTorpDamage = "" + stats.torpArgoDamage
            if (stats.totalArgoDamage > 1000000) {
                argoTorpDamage =
                    defaultDecimalFormatter.format(stats.totalArgoDamage / 1000000) + ctx.getString(
                        R.string.million
                    )
            }
            tvArgoTorpDamage.text = argoTorpDamage

            var buildingDamage = "" + stats.buildingDamage
            if (stats.buildingDamage > 1000000) {
                buildingDamage =
                    defaultDecimalFormatter.format(stats.buildingDamage / 1000000) + ctx.getString(R.string.million)
            }
            tvBuildingDamage.text = buildingDamage

            var scoutingDamage = "" + stats.scoutingDamage
            if (stats.scoutingDamage > 1000000) {
                scoutingDamage =
                    defaultDecimalFormatter.format(stats.scoutingDamage / 1000000) + ctx.getString(R.string.million)
            }
            tvSpottingDamage.text = scoutingDamage

            tvSpottingCount.text = "" + stats.shipsSpotted
            tvSuppressionCount.text = "" + stats.suppressionCount
            tvMaxSpotting.text = "" + stats.maxSpotted

            area.addView(view)
        }
    }
}
