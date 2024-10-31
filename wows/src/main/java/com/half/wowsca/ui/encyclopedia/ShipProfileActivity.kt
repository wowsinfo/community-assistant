package com.half.wowsca.ui.encyclopedia

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.getServerLanguage
import com.half.wowsca.CAApp.Companion.getServerType
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.alerts.Alert.createGeneralAlert
import com.half.wowsca.anim.ProgressBarAnimation
import com.half.wowsca.backend.GetShipEncyclopediaInfo
import com.half.wowsca.model.ShipInformation
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem
import com.half.wowsca.model.queries.ShipQuery
import com.half.wowsca.model.result.ShipResult
import com.half.wowsca.ui.CABaseActivity
import com.half.wowsca.ui.UIUtils.getNationText
import com.squareup.picasso.Picasso
import com.utilities.Utils.defaultDecimalFormatter
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.wtf
import com.utilities.views.SwipeBackLayout
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.Collections

/**
 * Created by slai4 on 10/31/2015.
 */
class ShipProfileActivity : CABaseActivity() {
    private var progress: View? = null
    private var imageView: ImageView? = null
    private var tvPrice: TextView? = null
    private var tvNationTier: TextView? = null
    private var tvDescirption: TextView? = null

    //progress stats
    private var aArtillery: View? = null
    private var modArtilery: TextView? = null
    private var progArtillery: ProgressBar? = null
    private var modSurvival: TextView? = null
    private var progSurvival: ProgressBar? = null
    private var aTorps: View? = null
    private var modTorps: TextView? = null
    private var progTorps: ProgressBar? = null
    private var aAA: View? = null
    private var modAA: TextView? = null
    private var progAA: ProgressBar? = null
    private var aAircraft: View? = null
    private var modAircraft: TextView? = null
    private var progAircraft: ProgressBar? = null
    private var modMobility: TextView? = null
    private var progMobility: ProgressBar? = null
    private var modConcealmeat: TextView? = null
    private var progConcealment: ProgressBar? = null

    // ship stats
    private var statsGunRange: TextView? = null
    private var statstorpRange: TextView? = null
    private var statsConcealRange: TextView? = null
    private var statsConcealRangePlane: TextView? = null
    private var statsShellDamage: TextView? = null
    private var statsSpeed: TextView? = null
    private var statsHealth: TextView? = null
    private var statsArtilleryFireRate: TextView? = null
    private var statsTorpFireRate: TextView? = null
    private var statsNumGuns: TextView? = null
    private var statsNumTorps: TextView? = null
    private var statsRudderShiftTime: TextView? = null
    private var statsSecondaryRange: TextView? = null
    private var statsAARange: TextView? = null
    private var statsTorpDamage: TextView? = null
    private var statsTorpSpeed: TextView? = null
    private var statsNumPlanes: TextView? = null
    private var statsTurningRadius: TextView? = null
    private var statsDispersion: TextView? = null
    private var statsRotation: TextView? = null
    private var statsArmor: TextView? = null
    private var statsAAGun: TextView? = null

    //ship stats text
    private var statsGunRangeText: TextView? = null
    private var statstorpRangeText: TextView? = null
    private var statsConcealRangeText: TextView? = null
    private var statsConcealRangePlaneText: TextView? = null
    private var statsShellDamageText: TextView? = null
    private var statsSpeedText: TextView? = null
    private var statsHealthText: TextView? = null
    private var statsArtilleryFireRateText: TextView? = null
    private var statsTorpFireRateText: TextView? = null
    private var statsNumGunsText: TextView? = null
    private var statsNumTorpsText: TextView? = null
    private var statsRudderShiftTimeText: TextView? = null
    private var statsSecondaryRangeText: TextView? = null
    private var statsAARangeText: TextView? = null
    private var statsTorpDamageText: TextView? = null
    private var statsTorpSpeedText: TextView? = null
    private var statsNumPlanesText: TextView? = null
    private var statsDispersionText: TextView? = null
    private var statsRotationText: TextView? = null
    private var statsArmorText: TextView? = null
    private var statsAAGunText: TextView? = null

    //average stats
    private var avgDamage: TextView? = null
    private var avgWinRate: TextView? = null
    private var avgKills: TextView? = null
    private var avgPlanes: TextView? = null

    //upgrades
    private var aUpgrades: View? = null
    private var llUpgrades: LinearLayout? = null

    //next ship
    private var aNextShip: View? = null
    private var llNextShips: LinearLayout? = null
    private var shipId: Long = 0
    private var shipServerInfo: String? = null
    private var llModule1: LinearLayout? = null
    private var llModule2: LinearLayout? = null
    private var tvModulesBottomText: TextView? = null

    private var scroll: ScrollView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encyclopedia_page)
        if (savedInstanceState != null) {
            shipId = savedInstanceState.getLong(SHIP_ID)
            shipServerInfo = savedInstanceState.getString(SHIP_DATA)
        } else {
            shipId = intent.getLongExtra(SHIP_ID, 0)
        }
        bindView()
    }

    private fun bindView() {
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        title = ""

        scroll = findViewById<View>(R.id.encyclopedia_scroll) as ScrollView?

        progress = findViewById<View>(R.id.encyclopedia_progress)

        imageView = findViewById<View>(R.id.encyclopedia_image) as ImageView?
        tvNationTier = findViewById<View>(R.id.encyclopedia_nation_tier) as TextView?
        tvPrice = findViewById<View>(R.id.encyclopedia_price) as TextView?
        tvDescirption = findViewById<View>(R.id.encyclopedia_description) as TextView?

        modAA = findViewById<View>(R.id.encyclopedia_progress_aa_text) as TextView?
        modAircraft = findViewById<View>(R.id.encyclopedia_progress_aircraft_text) as TextView?
        modArtilery = findViewById<View>(R.id.encyclopedia_progress_artillery_text) as TextView?
        modConcealmeat =
            findViewById<View>(R.id.encyclopedia_progress_concealment_text) as TextView?
        modMobility = findViewById<View>(R.id.encyclopedia_progress_mobility_text) as TextView?
        modSurvival = findViewById<View>(R.id.encyclopedia_progress_survival_text) as TextView?
        modTorps = findViewById<View>(R.id.encyclopedia_progress_torps_text) as TextView?

        progAA = findViewById<View>(R.id.encyclopedia_progress_aa) as ProgressBar?
        progAircraft = findViewById<View>(R.id.encyclopedia_progress_aircraft) as ProgressBar?
        progArtillery = findViewById<View>(R.id.encyclopedia_progress_artillery) as ProgressBar?
        progTorps = findViewById<View>(R.id.encyclopedia_progress_torps) as ProgressBar?
        progConcealment = findViewById<View>(R.id.encyclopedia_progress_concealment) as ProgressBar?
        progSurvival = findViewById<View>(R.id.encyclopedia_progress_survival) as ProgressBar?
        progMobility = findViewById<View>(R.id.encyclopedia_progress_mobility) as ProgressBar?

        aArtillery = findViewById<View>(R.id.encyclopedia_progress_artillery_area)
        aAA = findViewById<View>(R.id.encyclopedia_progress_aa_area)
        aAircraft = findViewById<View>(R.id.encyclopedia_progress_aircraft_area)
        aTorps = findViewById<View>(R.id.encyclopedia_progress_torps_area)

        statsArtilleryFireRate =
            findViewById<View>(R.id.encyclopedia_fire_rate_artillery) as TextView?
        statsConcealRange = findViewById<View>(R.id.encyclopedia_rang_concealment) as TextView?
        statsGunRange = findViewById<View>(R.id.encyclopedia_range_gun) as TextView?
        statsHealth = findViewById<View>(R.id.encyclopedia_health) as TextView?
        statsNumGuns = findViewById<View>(R.id.encyclopedia_number_of_guns) as TextView?
        statsNumTorps = findViewById<View>(R.id.encyclopedia_number_of_torps) as TextView?
        statsSpeed = findViewById<View>(R.id.encyclopedia_speed) as TextView?
        statsShellDamage = findViewById<View>(R.id.encyclopedia_range_spotting) as TextView?
        statsTorpFireRate = findViewById<View>(R.id.encyclopedia_fire_rate_torps) as TextView?
        statstorpRange = findViewById<View>(R.id.encyclopedia_range_torps) as TextView?
        statsConcealRangePlane =
            findViewById<View>(R.id.encyclopedia_rang_concealment_plane) as TextView?
        statsRudderShiftTime = findViewById<View>(R.id.encyclopedia_rudder_shift) as TextView?
        statsSecondaryRange = findViewById<View>(R.id.encyclopedia_range_secondaries) as TextView?
        statsAARange = findViewById<View>(R.id.encyclopedia_range_aa) as TextView?
        statsTorpDamage = findViewById<View>(R.id.encyclopedia_damage_torps) as TextView?
        statsTorpSpeed = findViewById<View>(R.id.encyclopedia_torps_speed) as TextView?
        statsNumPlanes = findViewById<View>(R.id.encyclopedia_number_of_planes) as TextView?
        statsTurningRadius = findViewById<View>(R.id.encyclopedia_turning_radius) as TextView?
        statsDispersion = findViewById<View>(R.id.encyclopedia_gun_dispersion) as TextView?
        statsRotation = findViewById<View>(R.id.encyclopedia_gun_rotation) as TextView?
        statsArmor = findViewById<View>(R.id.encyclopedia_armor) as TextView?
        statsAAGun = findViewById<View>(R.id.encyclopedia_aa_guns) as TextView?

        statsArtilleryFireRateText =
            findViewById<View>(R.id.encyclopedia_fire_rate_artillery_text) as TextView?
        statsConcealRangeText =
            findViewById<View>(R.id.encyclopedia_rang_concealment_text) as TextView?
        statsGunRangeText = findViewById<View>(R.id.encyclopedia_range_gun_text) as TextView?
        statsHealthText = findViewById<View>(R.id.encyclopedia_health_text) as TextView?
        statsNumGunsText = findViewById<View>(R.id.encyclopedia_number_of_guns_text) as TextView?
        statsNumTorpsText = findViewById<View>(R.id.encyclopedia_number_of_torps_text) as TextView?
        statsSpeedText = findViewById<View>(R.id.encyclopedia_speed_text) as TextView?
        statsShellDamageText =
            findViewById<View>(R.id.encyclopedia_range_spotting_text) as TextView?
        statsTorpFireRateText =
            findViewById<View>(R.id.encyclopedia_fire_rate_torps_text) as TextView?
        statstorpRangeText = findViewById<View>(R.id.encyclopedia_range_torps_text) as TextView?
        statsConcealRangePlaneText =
            findViewById<View>(R.id.encyclopedia_rang_concealment_plane_text) as TextView?
        statsRudderShiftTimeText =
            findViewById<View>(R.id.encyclopedia_rudder_shift_text) as TextView?
        statsSecondaryRangeText =
            findViewById<View>(R.id.encyclopedia_range_secondaries_text) as TextView?
        statsAARangeText = findViewById<View>(R.id.encyclopedia_range_aa_text) as TextView?
        statsTorpDamageText = findViewById<View>(R.id.encyclopedia_damage_torps_text) as TextView?
        statsTorpSpeedText = findViewById<View>(R.id.encyclopedia_torps_speed_text) as TextView?
        statsNumPlanesText =
            findViewById<View>(R.id.encyclopedia_number_of_planes_text) as TextView?
        statsDispersionText = findViewById<View>(R.id.encyclopedia_gun_dispersion_text) as TextView?
        statsRotationText = findViewById<View>(R.id.encyclopedia_gun_rotation_text) as TextView?
        statsArmorText = findViewById<View>(R.id.encyclopedia_armor_text) as TextView?
        statsAAGunText = findViewById<View>(R.id.encyclopedia_aa_guns_text) as TextView?

        avgDamage = findViewById<View>(R.id.encyclopedia_average_damage) as TextView?
        avgKills = findViewById<View>(R.id.encyclopedia_average_kills) as TextView?
        avgPlanes = findViewById<View>(R.id.encyclopedia_average_planes) as TextView?
        //        avgSurvival = (TextView) findViewById(R.id.encyclopedia_average_survival_rate);
        avgWinRate = findViewById<View>(R.id.encyclopedia_average_win_rate) as TextView?

        //        avgXP = (TextView) findViewById(R.id.encyclopedia_average_xp);
        aUpgrades = findViewById<View>(R.id.encyclopedia_upgrades_area)
        aNextShip = findViewById<View>(R.id.encyclopedia_next_ship_area)
        llUpgrades = findViewById<View>(R.id.encyclopedia_upgrades_container) as LinearLayout?
        llNextShips = findViewById<View>(R.id.encyclopedia_next_ship_container) as LinearLayout?

        llModule1 = findViewById<View>(R.id.encyclopedia_module_list_1) as LinearLayout?
        llModule2 = findViewById<View>(R.id.encyclopedia_module_list_2) as LinearLayout?
        tvModulesBottomText = findViewById<View>(R.id.encyclopedia_module_bottom_text) as TextView?

        val view = findViewById<View>(R.id.encyclopedia_warshipstats_area)

        view!!.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse("https://warships.today/na/help/warships_today_rating"))
            startActivity(i)
        }
        swipeBackLayout!!.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }


    private fun initView() {
        if (shipId != 0L) {
            val shipInfo = infoManager!!.getShipInfo(applicationContext)[shipId]
            if (shipInfo != null) {
                title = shipInfo.name
                val nation = getNationText(applicationContext, shipInfo.nation)
                tvNationTier!!.text =
                    nation + getString(R.string.encyclopedia_nation_tier) + " " + shipInfo.tier
                Picasso.get().load(shipInfo.bestImage).error(R.drawable.ic_missing_image)
                    .into(imageView)

                val formatter = DecimalFormat(PATTERN)
                if (shipInfo.isPremium) {
                    tvPrice!!.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.premium_shade
                        )
                    )
                    if (shipInfo.goldPrice > 0) {
                        tvPrice!!.text =
                            formatter.format(shipInfo.goldPrice.toLong()) + getString(R.string.gold)
                    } else {
                        tvPrice!!.setText(R.string.price_not_known)
                    }
                } else {
                    if (shipInfo.price > 0) {
                        tvPrice!!.text =
                            formatter.format(shipInfo.price.toLong()) + getString(R.string.credits)
                    } else {
                        tvPrice!!.setText(R.string.price_not_known)
                    }
                }
                if (!TextUtils.isEmpty(shipInfo.description)) {
                    tvDescirption!!.visibility = View.VISIBLE
                    tvDescirption!!.text = shipInfo.description
                } else {
                    tvDescirption!!.visibility = View.GONE
                }

                createModuleGrid()

                //upgrades
                if (shipInfo.equipments != null && shipInfo.equipments.size > 0) {
                    aUpgrades!!.visibility = View.VISIBLE
                    createUpgrades(shipInfo)
                } else {
                    aUpgrades!!.visibility = View.GONE
                }

                //nextship
                if (shipInfo.nextShipIds != null && shipInfo.nextShipIds.size > 0) {
                    aNextShip!!.visibility = View.VISIBLE
                    createNextShip(shipInfo)
                } else {
                    aNextShip!!.visibility = View.GONE
                }
            }
            progress!!.visibility = View.GONE
            if (!TextUtils.isEmpty(shipServerInfo)) {
                wtf("Encyclopedia", "shipInfo = $shipServerInfo")

                val info = ShipInformation()
                info.parse(shipServerInfo)
                if (info.armour != null) {
                    //survival

                    statsHealth!!.text = "" + info.health
                    val survivalTotal = info.survivalHealth
                    modSurvival!!.text = "" + survivalTotal
                    animate(progSurvival, survivalTotal)
                    val planes_amount = info.planesAmount
                    if (planes_amount > 0) {
                        statsNumPlanes!!.text = planes_amount.toString() + ""
                    } else {
                        disableView(statsNumPlanesText)
                    }

                    val strbug = """
                        ${grabMinMax(info.armour, "range", getString(R.string.range_armor))}
                        ${grabMinMax(info.armour, "deck", getString(R.string.deck_armor))}
                        ${
                        grabMinMax(
                            info.armour,
                            "extremities",
                            getString(R.string.bow_stern_armor)
                        )
                    }
                        ${
                        grabMinMax(
                            info.armour,
                            "casemate",
                            getString(R.string.gun_casemate_armor)
                        )
                    }
                        ${grabMinMax(info.armour, "citadel", getString(R.string.citadel_armor))}
                        """.trimIndent()
                    statsArmor!!.text = strbug

                    //artillery
                    val artileryTotal = info.artilleryTotal
                    if (artileryTotal > 0) {
                        modArtilery!!.text = artileryTotal.toString() + ""
                        animate(progArtillery, artileryTotal)
                        aArtillery!!.visibility = View.VISIBLE
                    } else {
                        aArtillery!!.visibility = View.GONE
                    }

                    //torps
                    val torpsTotal = info.torpTotal
                    if (torpsTotal > 0) {
                        modTorps!!.text = torpsTotal.toString() + ""
                        animate(progTorps, torpsTotal)
                        aTorps!!.visibility = View.VISIBLE
                    } else {
                        aTorps!!.visibility = View.GONE
                    }

                    //anti-aircraft
                    val aaTotal = info.antiAirTotal
                    if (aaTotal > 0) {
                        modAA!!.text = aaTotal.toString() + ""
                        animate(progAA, aaTotal)
                        aAA!!.visibility = View.VISIBLE
                    } else {
                        aAA!!.visibility = View.GONE
                    }

                    //aircraft
                    val aircraftTotal = info.aircraftTotal
                    if (aircraftTotal > 0) {
                        modAircraft!!.text = aircraftTotal.toString() + ""
                        animate(progAircraft, aircraftTotal)
                        aAircraft!!.visibility = View.VISIBLE
                    } else {
                        aAircraft!!.visibility = View.GONE
                    }

                    //mobility
                    val mobilityTotal = info.mobilityTotal
                    modMobility!!.text = "" + mobilityTotal
                    animate(progMobility, mobilityTotal)
                    statsTurningRadius!!.text =
                        info.turningRadius.toString() + getString(R.string.meters)

                    //concealment
                    val concealTotal = info.concealmentTotal
                    modConcealmeat!!.text = "" + concealTotal
                    animate(progConcealment, concealTotal)

                    statsSpeed!!.text = info.speed.toString() + ""
                    statsRudderShiftTime!!.text =
                        info.rudderTime.toString() + getString(R.string.seconds)

                    if (info.fire_control != null) {
                        statsGunRange!!.text =
                            info.artiDistance.toString() + getString(R.string.kilometers)
                    } else {
                        disableView(statsGunRangeText)
                    }

                    if (info.atba != null) {
                        statsSecondaryRange!!.text =
                            info.secondaryRange.toString() + getString(R.string.kilometers)
                    } else {
                        disableView(statsSecondaryRangeText)
                    }

                    if (info.torps != null) {
                        statstorpRange!!.text =
                            info.torpDistance.toString() + getString(R.string.kilometers)
                        val reloadTime = info.torpReload
                        var suffix = "s"
                        if (reloadTime > 60) {
                            suffix = " mins"
                        }
                        statsTorpFireRate!!.text =
                            oneDepthDecimalFormatter.format(reloadTime / 60) + suffix
                        statsTorpDamage!!.text = info.torpMaxDamage.toString() + ""
                        statsTorpSpeed!!.text =
                            info.torpSpeed.toString() + getString(R.string.kilometers_per_second)
                        val barrels = info.torpBarrels
                        val turrets = info.torpGuns
                        statsNumTorps!!.text =
                            (barrels * turrets).toString() + " " + barrels + "x" + turrets
                    } else if (info.torpedoBombers != null) {
                        statstorpRange!!.text =
                            info.getTorpBDistance().toString() + getString(R.string.kilometers)
                        statsTorpFireRate!!.text =
                            oneDepthDecimalFormatter.format(info.torpBprepareTime) + getString(R.string.seconds)
                        statsTorpDamage!!.text = info.torpBDamage.toString() + ""
                        statsTorpSpeed!!.text =
                            info.torpBMaxSpeed.toString() + getString(R.string.kilometers_per_second)
                        if (info.flightControl != null) {
                            statsNumTorps!!.text =
                                (info.fighterSquadrons + info.bomberSquadrons + info.torpedoSquadrons).toString() + ""
                        }
                    } else {
                        disableView(statsTorpFireRateText)
                        disableView(statstorpRangeText)
                        disableView(statsTorpDamage)
                        disableView(statsTorpSpeedText)
                        disableView(statsNumTorpsText)
                        disableView(statsTorpDamageText)
                    }

                    if (info.concealment != null) {
                        statsConcealRange!!.text =
                            info.concealmentDistanceShip.toString() + getString(R.string.kilometers)
                        statsConcealRangePlane!!.text =
                            info.concealmentDistancePlane.toString() + getString(R.string.kilometers)
                    } else {
                        disableView(statsConcealRangeText)
                        disableView(statsConcealRangePlaneText)
                    }

                    if (info.artillery != null) {
                        statsArtilleryFireRate!!.text = info.artiGunRate.toString() + ""
                        val barrels = info.artiBarrels
                        val turrets = info.artiTurrets
                        statsNumGuns!!.text =
                            (barrels * turrets).toString() + " " + barrels + "x" + turrets
                        statsDispersion!!.text = info.artiMaxDispersion.toString() + "m"
                        statsRotation!!.text = info.artiRotation.toString() + "s"
                        val shells = info.artillery.optJSONObject("shells")
                        if (shells != null) {
                            val ap = shells.optJSONObject("AP")
                            val he = shells.optJSONObject("HE")
                            val sb = StringBuilder()
                            if (ap != null) {
                                sb.append("AP - " + info.artiAPDmg)
                                sb.append("\n")
                            }
                            if (he != null) {
                                sb.append(
                                    "HE - " + info.artiHEDmg + " " + oneDepthDecimalFormatter.format(
                                        info.artiHEBurnProb
                                    ) + "%"
                                )
                            }
                            val shellDamage = sb.toString()
                            if (!TextUtils.isEmpty(shellDamage)) {
                                statsShellDamage!!.text = shellDamage
                            } else {
                            }
                        }
                    } else if (info.diveBombers != null) {
                        statsArtilleryFireRateText!!.setText(R.string.encyclopedia_db_prep_time)
                        statsArtilleryFireRate!!.text =
                            info.diveBPrepareTime.toString() + getString(R.string.seconds)
                        statsShellDamageText!!.setText(R.string.encyclopedia_db_damage)
                        statsShellDamage!!.text =
                            info.diveBMaxDmg.toString() + " - " + oneDepthDecimalFormatter.format(
                                info.diveBBurnProbably
                            ) + "%"
                        //do something with numGuns
                        disableView(statsNumGunsText)
                        disableView(statsRotationText)
                        disableView(statsDispersionText)
                    } else {
                        disableView(statsArtilleryFireRateText)
                        disableView(statsShellDamageText)
                        disableView(statsNumGunsText)
                        disableView(statsRotationText)
                        disableView(statsDispersionText)
                    }

                    if (info.aa != null) {
                        val slots = info.aa.optJSONObject("slots")
                        if (slots != null) {
                            if (slots.length() > 0) {
                                val strb = StringBuilder()
                                val itea = slots.keys()
                                var topAARange = 0.0
                                while (itea.hasNext()) {
                                    val aSlot = slots.optJSONObject(itea.next())
                                    val aaRange = aSlot.optDouble("distance")
                                    if (aaRange > topAARange) {
                                        topAARange = aaRange
                                    }
                                    val avgDmg = aSlot.optDouble("avg_damage")
                                    val caliber = aSlot.optDouble("caliber")
                                    val guns = aSlot.optInt("guns")
                                    strb.append(
                                        guns.toString() + "  " + Math.round(caliber) + getString(
                                            R.string.millimeters
                                        ) + "  " + Math.round(avgDmg) + " " + getString(R.string.damage_per_second) + "  " + aaRange + getString(
                                            R.string.kilometers
                                        )
                                    )
                                    strb.append("\n")
                                }
                                statsAAGun!!.text = strb.toString().trim { it <= ' ' }
                                statsAARange!!.text =
                                    topAARange.toString() + getString(R.string.kilometers)
                            }
                        }
                    } else {
                        disableView(statsAARangeText)
                        disableView(statsAAGunText)
                    }
                }
                val stats = infoManager!!.getShipStats(applicationContext)[shipId]
                if (stats != null) {
                    avgDamage!!.text = oneDepthDecimalFormatter.format(stats.dmg_dlt.toDouble())
                    avgWinRate!!.text =
                        oneDepthDecimalFormatter.format((stats.wins * 100).toDouble()) + "%"
                    avgKills!!.text = defaultDecimalFormatter.format(stats.frags.toDouble())
                    avgPlanes!!.text = oneDepthDecimalFormatter.format(stats.pls_kd.toDouble())
                } else {
                    disableView(avgDamage)
                    disableView(avgWinRate)
                    disableView(avgKills)
                    disableView(avgPlanes)
                }
            } else {
                getShipInfo()
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.encyclopedia_ship_not_found,
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun grabMinMax(overall: JSONObject, value: String, text: String): String {
        val armorPack = overall.optJSONObject(value)
        val min = armorPack.optString("min")
        val max = armorPack.optString("max")
        return "$text $min-$max"
    }

    private fun animate(bar: ProgressBar?, value: Int) {
        val anim = ProgressBarAnimation(bar!!, 0f, value.toFloat())
        anim.duration = 1500
        bar.startAnimation(anim)
    }

    private fun createModuleGrid() {
        llModule1!!.removeAllViews()
        llModule2!!.removeAllViews()

        llModule1!!.post(object : Runnable {
            override fun run() {
                val shipInfo = infoManager!!.getShipInfo(applicationContext)[shipId]

                buildDefaultModuleList(shipInfo)

                val artillery = shipInfo!!.items[MODULE_LIST!![GetShipEncyclopediaInfo.ARTILLERY]]
                val torps = shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.TORPEDOES]]
                val fireControl =
                    shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.FIRE_CONTROL]]
                val flightControl =
                    shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.FLIGHT_CONTROL]]
                val hull = shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.HULL]]
                val engine = shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.ENGINE]]
                val fighter = shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.FIGHTER]]
                val diveBomber = shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.DIVE_BOMBER]]
                val torpBomber =
                    shipInfo.items[MODULE_LIST!![GetShipEncyclopediaInfo.TORPEDO_BOMBER]]

                // Add needed items to list
                val hasOptions: MutableList<Boolean> = ArrayList()
                val items: MutableList<ShipModuleItem> = ArrayList()

                addNecessaryModules(
                    shipInfo,
                    artillery,
                    torps,
                    fireControl,
                    flightControl,
                    hull,
                    engine,
                    fighter,
                    diveBomber,
                    torpBomber,
                    hasOptions,
                    items
                )

                //                Dlog.d("ModulesLIST", MODULE_LIST.toString());
//                Dlog.d("Modules", items.toString());
//                Dlog.d("hasOptions", hasOptions.toString());
                val hasAnOption = buildModuleLists(hasOptions, items)

                if (hasAnOption) {
                    tvModulesBottomText!!.visibility = View.VISIBLE
                } else {
                    tvModulesBottomText!!.visibility = View.GONE
                }
            }

            private fun addNecessaryModules(
                shipInfo: ShipInfo?,
                artillery: ShipModuleItem?,
                torps: ShipModuleItem?,
                fireControl: ShipModuleItem?,
                flightControl: ShipModuleItem?,
                hull: ShipModuleItem?,
                engine: ShipModuleItem?,
                fighter: ShipModuleItem?,
                diveBomber: ShipModuleItem?,
                torpBomber: ShipModuleItem?,
                hasOptions: MutableList<Boolean>,
                items: MutableList<ShipModuleItem>
            ) {
                if (artillery != null) {
                    items.add(artillery)
                    hasOptions.add(shipInfo!!.artillery.size > 1)
                }
                if (torps != null) {
                    items.add(torps)
                    hasOptions.add(shipInfo!!.torps.size > 1)
                }
                if (fireControl != null) {
                    items.add(fireControl)
                    hasOptions.add(shipInfo!!.fireControl.size > 1)
                }
                if (flightControl != null) {
                    items.add(flightControl)
                    hasOptions.add(shipInfo!!.flightControl.size > 1)
                }
                if (hull != null) {
                    items.add(hull)
                    hasOptions.add(shipInfo!!.hull.size > 1)
                }
                if (engine != null) {
                    items.add(engine)
                    hasOptions.add(shipInfo!!.engine.size > 1)
                }
                if (fighter != null) {
                    items.add(fighter)
                    hasOptions.add(shipInfo!!.fighter.size > 1)
                }
                if (diveBomber != null) {
                    items.add(diveBomber)
                    hasOptions.add(shipInfo!!.diveBomber.size > 1)
                }
                if (torpBomber != null) {
                    items.add(torpBomber)
                    hasOptions.add(shipInfo!!.torpBomb.size > 1)
                }
            }

            private fun buildDefaultModuleList(shipInfo: ShipInfo?) {
                if (MODULE_LIST == null) {
                    val defaultList = mutableMapOf<String, Long>()
                    defaultList[GetShipEncyclopediaInfo.ARTILLERY] =
                        getBaseModuleToList(shipInfo, shipInfo!!.artillery)
                    defaultList[GetShipEncyclopediaInfo.TORPEDOES] =
                        getBaseModuleToList(shipInfo, shipInfo.torps)
                    defaultList[GetShipEncyclopediaInfo.FIRE_CONTROL] =
                        getBaseModuleToList(shipInfo, shipInfo.fireControl)
                    defaultList[GetShipEncyclopediaInfo.FLIGHT_CONTROL] =
                        getBaseModuleToList(shipInfo, shipInfo.flightControl)
                    defaultList[GetShipEncyclopediaInfo.HULL] =
                        getBaseModuleToList(shipInfo, shipInfo.hull)
                    defaultList[GetShipEncyclopediaInfo.ENGINE] =
                        getBaseModuleToList(shipInfo, shipInfo.engine)
                    defaultList[GetShipEncyclopediaInfo.FIGHTER] =
                        getBaseModuleToList(shipInfo, shipInfo.fighter)
                    defaultList[GetShipEncyclopediaInfo.DIVE_BOMBER] =
                        getBaseModuleToList(shipInfo, shipInfo.diveBomber)
                    defaultList[GetShipEncyclopediaInfo.TORPEDO_BOMBER] =
                        getBaseModuleToList(shipInfo, shipInfo.torpBomb)
                    MODULE_LIST = defaultList
                }
            }

            private fun buildModuleLists(
                hasOptions: List<Boolean>,
                items: List<ShipModuleItem>
            ): Boolean {
                var hasAnOption = false
                for (i in items.indices) {
                    var parent = llModule1
                    if (i % 2 == 1) parent = llModule2

                    val convertView = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_ship_module, parent, false)
                    val tv = convertView.findViewById<TextView>(R.id.list_module_top)
                    val tvText = convertView.findViewById<TextView>(R.id.list_module_text)

                    val item = items[i]
                    val hasOpt = hasOptions[i]

                    if (hasOpt) {
                        hasAnOption = true
                        convertView.setBackgroundResource(R.drawable.encyclopedia_module_white)
                    } else {
                        convertView.setBackgroundResource(R.drawable.compare_normal_grid)
                    }

                    val sb = StringBuilder()
                    sb.append(item.name)
                    if (!item.isDefault) {
                        val format = DecimalFormat(PATTERN)
                        if (item.price_xp > 0) sb.append(
                            """
    
    ${format.format(item.price_xp)}xp
    """.trimIndent()
                        )
                        if (item.price_credits > 0) sb.append(
                            """
    
    ${format.format(item.price_credits)}c
    """.trimIndent()
                        )
                    }

                    tvText.text = sb.toString()

                    cleanModuleTitle(tv, item.type)

                    convertView.tag = item.id

                    convertView.setOnClickListener { v ->
                        val id = v.tag as Long
                        if (id != null) {
                            val shipInfo = infoManager!!.getShipInfo(applicationContext)[shipId]
                            val item = shipInfo!!.items[id]
                            var typeIds: List<Long?> = ArrayList()
                            when (item!!.type) {
                                "Suo" -> typeIds = shipInfo.fireControl
                                "FlightControl" -> typeIds = shipInfo.flightControl
                                "DiveBomber" -> typeIds = shipInfo.diveBomber
                                "Fighter" -> typeIds = shipInfo.fighter
                                "Artillery" -> typeIds = shipInfo.artillery
                                "Hull" -> typeIds = shipInfo.hull
                                "TorpedoBomber" -> typeIds = shipInfo.torpBomb
                                "Torpedoes" -> typeIds = shipInfo.torps
                                "Engine" -> typeIds = shipInfo.engine
                            }
                            Collections.sort(typeIds) { lhs, rhs ->
                                val lhsItem = shipInfo.items[lhs]
                                val rhsItem = shipInfo.items[rhs]
                                lhsItem!!.name.compareTo(rhsItem!!.name, ignoreCase = true)
                            }
                            if (typeIds.size > 1) {
                                val menu = PopupMenu(this@ShipProfileActivity, v)
                                menu.gravity = Gravity.CENTER
                                val mapOfItems: MutableMap<String, ShipModuleItem> = HashMap()
                                val m = menu.menu
                                for (i in typeIds.indices) {
                                    val it = shipInfo.items[typeIds[i]]
                                    if (it != null) {
                                        m.add(it.name)
                                        mapOfItems[it.name] = it
                                    }
                                }

                                menu.setOnMenuItemClickListener(object :
                                    PopupMenu.OnMenuItemClickListener {
                                    override fun onMenuItemClick(item: MenuItem): Boolean {
                                        val i = mapOfItems[item.title]
                                        when (i!!.type) {
                                            "Suo" -> MODULE_LIST!![GetShipEncyclopediaInfo.FIRE_CONTROL] =
                                                i.id

                                            "FlightControl" -> MODULE_LIST!![GetShipEncyclopediaInfo.FLIGHT_CONTROL] =
                                                i.id

                                            "DiveBomber" -> MODULE_LIST!![GetShipEncyclopediaInfo.DIVE_BOMBER] =
                                                i.id

                                            "Fighter" -> MODULE_LIST!![GetShipEncyclopediaInfo.FIGHTER] =
                                                i.id

                                            "Artillery" -> MODULE_LIST!![GetShipEncyclopediaInfo.ARTILLERY] =
                                                i.id

                                            "Hull" -> MODULE_LIST!![GetShipEncyclopediaInfo.HULL] =
                                                i.id

                                            "TorpedoBomber" -> MODULE_LIST!![GetShipEncyclopediaInfo.TORPEDO_BOMBER] =
                                                i.id

                                            "Torpedoes" -> MODULE_LIST!![GetShipEncyclopediaInfo.TORPEDOES] =
                                                i.id

                                            "Engine" -> MODULE_LIST!![GetShipEncyclopediaInfo.ENGINE] =
                                                i.id
                                        }
                                        //Display list of options and update the view.
                                        clearScreen()
                                        //clear data from screen
                                        getShipInfo()
                                        return false
                                    }
                                })
                                menu.show()
                            }
                        }
                    }

                    parent!!.addView(convertView)
                }
                return hasAnOption
            }
        })
    }

    private fun clearScreen() {
        try {
            scroll!!.smoothScrollTo(0, 0)
        } catch (e: Exception) {
        }

        llModule1!!.removeAllViews()
        llModule2!!.removeAllViews()

        progAA!!.progress = 0
        progAircraft!!.progress = 0
        progArtillery!!.progress = 0
        progConcealment!!.progress = 0
        progMobility!!.progress = 0
        progSurvival!!.progress = 0
        progTorps!!.progress = 0

        modAA!!.text = ""
        modAircraft!!.text = ""
        modArtilery!!.text = ""
        modConcealmeat!!.text = ""
        modMobility!!.text = ""
        modSurvival!!.text = ""
        modTorps!!.text = ""

        avgDamage!!.text = ""
        avgKills!!.text = ""
        avgPlanes!!.text = ""
        avgWinRate!!.text = ""

        statsAARange!!.text = ""
        statsArtilleryFireRate!!.text = ""
        statsConcealRange!!.text = ""
        statsConcealRangePlane!!.text = ""
        statsGunRange!!.text = ""
        statsHealth!!.text = ""
        statsNumGuns!!.text = ""
        statstorpRange!!.text = ""
        statsTorpSpeed!!.text = ""
        statsSecondaryRange!!.text = ""
        statsShellDamage!!.text = ""
        statsSpeed!!.text = ""
        statsTorpFireRate!!.text = ""
        statsNumTorps!!.text = ""
        statsNumPlanes!!.text = ""
        statsRudderShiftTime!!.text = ""
        statsTurningRadius!!.text = ""
        statsAAGun!!.text = ""
        statsArmor!!.text = ""
    }

    private fun cleanModuleTitle(tv: TextView, title: String) {
        var title: String? = title
        when (title) {
            "Suo" -> title = getString(R.string.fire_control)
            "FlightControl" -> title = getString(R.string.flight_control)
            "TorpedoBomber" -> title = getString(R.string.torpedo_bomber)
            "DiveBomber" -> title = getString(R.string.dive_bomber)
        }
        tv.text = title
    }

    private fun getBaseModuleToList(info: ShipInfo?, modules: List<Long>?): Long {
        if (modules != null && modules.size > 0) {
            for (i in modules.indices) {
                val item = info!!.items[modules[i]]
                if (item!!.isDefault) {
                    return item.id
                }
            }
            return modules[0]
        } else return 0
    }

    private fun disableView(tv: TextView?) {
        tv!!.alpha = 0.5f
    }

    private fun createNextShip(shipInfo: ShipInfo) {
        llNextShips!!.removeAllViews()
        val shipIds = shipInfo.nextShipIds
        llNextShips!!.post {
            for (l in shipIds) {
                val info = infoManager!!.getShipInfo(applicationContext)[l]
                if (info != null) {
                    val view = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_next_ship, llNextShips, false)
                    val image = view.findViewById<ImageView>(R.id.list_next_ship_image)
                    val text = view.findViewById<TextView>(R.id.list_next_ship_text)

                    text.text = info.name
                    Picasso.get().load(info.image).error(R.drawable.ic_missing_image).into(image)

                    view.tag = l
                    view.setOnClickListener { v ->
                        val id = v.tag as Long
                        val i = Intent(applicationContext, ShipProfileActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra(SHIP_ID, id)
                        startActivity(i)
                    }
                    llNextShips!!.addView(view)
                }
            }
        }
    }

    private fun createUpgrades(shipInfo: ShipInfo) {
        llUpgrades!!.removeAllViews()
        val equipeIds = shipInfo.equipments
        llUpgrades!!.post {
            for (l in equipeIds) {
                val info = infoManager!!.getUpgrades(applicationContext)[l]
                if (info != null) {
                    val view = LayoutInflater.from(applicationContext)
                        .inflate(R.layout.list_upgrades, llNextShips, false)
                    val image = view.findViewById<ImageView>(R.id.list_upgrades_image)
                    Picasso.get().load(info.image).error(R.drawable.ic_missing_image).into(image)
                    view.tag = l
                    view.setOnClickListener { v ->
                        val id = v.tag as Long
                        val info = infoManager!!.getUpgrades(applicationContext)[id]
                        val ctx: Context = this@ShipProfileActivity
                        val formatter = DecimalFormat(PATTERN)
                        createGeneralAlert(
                            ctx,
                            info!!.name,
                            info.description + getString(R.string.encyclopedia_upgrade_cost) + formatter.format(
                                info.price.toLong()
                            ),
                            getString(R.string.dismiss)
                        )
                    }
                    llUpgrades!!.addView(view)
                }
            }
        }
    }

    private fun getShipInfo() {
        progress!!.visibility = View.VISIBLE
        val query = ShipQuery()
        query.server = getServerType(applicationContext)
        query.shipId = shipId
        query.language = getServerLanguage(applicationContext)
        query.modules = MODULE_LIST

        val async = GetShipEncyclopediaInfo()
        async.execute(query)
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SHIP_ID, shipId)
        outState.putString(SHIP_DATA, shipServerInfo)
    }

    @Subscribe
    fun onShipRecieveInfo(result: ShipResult) {
        if (result.shipInfo != null && result.shipId == shipId) {
            shipServerInfo = result.shipInfo
            progress!!.post { initView() }
        } else if (result.shipId == shipId) {
            progress!!.post {
                Toast.makeText(applicationContext, R.string.failed_to_grab_data, Toast.LENGTH_SHORT)
                    .show()
                shipServerInfo = "fail"
                initView()
            }
        }
    }

    companion object {
        const val SHIP_ID: String = "shipid"
        const val SHIP_DATA: String = "shipData"
        const val PATTERN: String = "###,###,###"
        var MODULE_LIST: MutableMap<String, Long>? = null
    }
}
