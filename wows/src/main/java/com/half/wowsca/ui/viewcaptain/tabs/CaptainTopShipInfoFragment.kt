package com.half.wowsca.ui.viewcaptain.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.half.wowsca.CAApp.Companion.eventBus
import com.half.wowsca.CAApp.Companion.infoManager
import com.half.wowsca.R
import com.half.wowsca.interfaces.ICaptain
import com.half.wowsca.model.BatteryStats
import com.half.wowsca.model.Captain
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.ui.CAFragment
import com.half.wowsca.ui.UIUtils.setShipImage
import com.half.wowsca.ui.UIUtils.setUpCard
import com.utilities.Utils.oneDepthDecimalFormatter
import com.utilities.logging.Dlog.d
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat

/**
 * Created by slai4 on 1/20/2016.
 */
class CaptainTopShipInfoFragment() : CAFragment() {
    private var tvArmamentMain: TextView? = null
    private var tvArmamentTorps: TextView? = null
    private var tvArmamentAircraft: TextView? = null
    private var tvArmamentOthers: TextView? = null

    private var ivTopKills: ImageView? = null
    private var tvTopKills: TextView? = null
    private var tvTopKillsName: TextView? = null

    private var ivTopDamage: ImageView? = null
    private var tvTopDamage: TextView? = null
    private var tvTopDamageName: TextView? = null

    private var ivTopXP: ImageView? = null
    private var tvTopXP: TextView? = null
    private var tvTopXPName: TextView? = null

    private var ivTopPlanes: ImageView? = null
    private var tvTopPlanes: TextView? = null
    private var tvTopPlanesName: TextView? = null

    private var ivTopSurvivalRate: ImageView? = null
    private var tvTopSurvivalRate: TextView? = null
    private var tvTopSurvivalRateName: TextView? = null

    private var ivTopWinRate: ImageView? = null
    private var tvTopWinRate: TextView? = null
    private var tvTopWinRateName: TextView? = null

    private var ivTopTotalKills: ImageView? = null
    private var tvTopTotalKills: TextView? = null
    private var tvTopTotalKillsName: TextView? = null

    private var ivTopAvgDmg: ImageView? = null
    private var tvTopAvgDmg: TextView? = null
    private var tvTopAvgDmgName: TextView? = null

    private var ivTopPlayed: ImageView? = null
    private var tvTopPlayed: TextView? = null
    private var tvTopPlayedName: TextView? = null

    private var ivTopDistance: ImageView? = null
    private var tvTopDistance: TextView? = null
    private var tvTopDistanceName: TextView? = null

    private var ivTopTotalDmg: ImageView? = null
    private var tvTopTotalDmg: TextView? = null
    private var tvTopTotalDmgName: TextView? = null

    private var ivTopTotalExp: ImageView? = null
    private var tvTopTotalExp: TextView? = null
    private var tvTopTotalExpName: TextView? = null

    private var ivTopSurvivedWins: ImageView? = null
    private var tvTopSurvivedWins: TextView? = null
    private var tvTopSurvivedWinsName: TextView? = null

    private var ivTopTotalPlanes: ImageView? = null
    private var tvTopTotalPlanes: TextView? = null
    private var tvTopTotalPlanesName: TextView? = null

    private var ivTopCARating: ImageView? = null
    private var tvTopCaRating: TextView? = null
    private var tvTopCARatingName: TextView? = null

    private var ivTopMBAccuracy: ImageView? = null
    private var tvTopMBAccuracy: TextView? = null
    private var tvTopMBAccuracyName: TextView? = null

    private var ivTopTBAccuracy: ImageView? = null
    private var tvTopTBAccuracy: TextView? = null
    private var tvTopTBAccuracyName: TextView? = null

    private var ivTopSpotted: ImageView? = null
    private var tvTopSpotted: TextView? = null
    private var tvTopSpottedName: TextView? = null

    private var ivTopScoutingDamage: ImageView? = null
    private var tvTopScoutingDamage: TextView? = null
    private var tvTopScoutingDamageName: TextView? = null

    private var ivTopTanking: ImageView? = null
    private var tvTopTanking: TextView? = null
    private var tvTopTankingName: TextView? = null

    private var ivTopTorpTanking: ImageView? = null
    private var tvTopTorpTanking: TextView? = null
    private var tvTopTorpTankingName: TextView? = null

    private var tvBatteryMainDestroyed: TextView? = null
    private var tvBatteryMainHitRatio: TextView? = null
    private var tvBatteryMainShots: TextView? = null
    private var ivBatteryMain: ImageView? = null
    private var tvBatteryMainShipName: TextView? = null
    private var tvBatteryMainShipNumber: TextView? = null

    private var tvBatterySecondaryDestroyed: TextView? = null
    private var tvBatterySecondaryHitRatio: TextView? = null
    private var tvBatterySecondaryShots: TextView? = null
    private var ivBatterySecondary: ImageView? = null
    private var tvBatterySecondaryShipName: TextView? = null
    private var tvBatterySecondaryShipNumber: TextView? = null

    private var tvBatteryTorpsDestroyed: TextView? = null
    private var tvBatteryTorpsHitRatio: TextView? = null
    private var tvBatteryTorpsShots: TextView? = null
    private var ivBatteryTorps: ImageView? = null
    private var tvBatteryTorpsShipName: TextView? = null
    private var tvBatteryTorpsShipNumber: TextView? = null

    private var tvBatteryAircraftDestroyed: TextView? = null
    private var tvBatteryAircraftHitRatio: TextView? = null
    private var tvBatteryAircraftShots: TextView? = null
    private var ivBatteryAircraft: ImageView? = null
    private var tvBatteryAircraftShipName: TextView? = null
    private var tvBatteryAircraftShipNumber: TextView? = null

    private var tvBatteryRammingDestroyed: TextView? = null
    private var tvBatteryRammingHitRatio: TextView? = null
    private var tvBatteryRammingShots: TextView? = null
    private var ivBatteryRamming: ImageView? = null
    private var tvBatteryRammingShipName: TextView? = null
    private var tvBatteryRammingShipNumber: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_captain_top_ship_info, container, false)
        bindView(view)
        return view
    }

    private fun bindView(view: View) {
        tvArmamentMain = view.findViewById(R.id.captain_details_battery_kills_main)
        tvArmamentAircraft = view.findViewById(R.id.captain_details_battery_kills_aircraft)
        tvArmamentOthers = view.findViewById(R.id.captain_details_battery_kills_other)
        tvArmamentTorps = view.findViewById(R.id.captain_details_battery_kills_torps)

        //        if(CAApp.isLightTheme(view.getContext())){
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_main_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_aircraft_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_torps_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//            ((ImageView) view.findViewById(R.id.captain_details_battery_kills_other_iv)).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.top_background), PorterDuff.Mode.MULTIPLY);
//        }
        ivTopKills = view.findViewById(R.id.captain_details_top_kills_icon)
        tvTopKills = view.findViewById(R.id.captain_details_top_kills_text)
        tvTopKillsName = view.findViewById(R.id.captain_details_top_kills_name_text)

        ivTopDamage = view.findViewById(R.id.captain_details_top_damage_icon)
        tvTopDamage = view.findViewById(R.id.captain_details_top_damage_text)
        tvTopDamageName = view.findViewById(R.id.captain_details_top_damage_name_text)

        ivTopXP = view.findViewById(R.id.captain_details_top_xp_icon)
        tvTopXP = view.findViewById(R.id.captain_details_top_xp_text)
        tvTopXPName = view.findViewById(R.id.captain_details_top_xp_name_text)

        ivTopPlanes = view.findViewById(R.id.captain_details_top_planes_icon)
        tvTopPlanes = view.findViewById(R.id.captain_details_top_planes_text)
        tvTopPlanesName = view.findViewById(R.id.captain_details_top_planes_name_text)

        //new ones
        ivTopSurvivalRate = view.findViewById(R.id.captain_details_top_survival_rate_icon)
        tvTopSurvivalRate = view.findViewById(R.id.captain_details_top_survival_rate_text)
        tvTopSurvivalRateName = view.findViewById(R.id.captain_details_top_survival_rate_name_text)

        ivTopWinRate = view.findViewById(R.id.captain_details_top_win_rate_icon)
        tvTopWinRate = view.findViewById(R.id.captain_details_top_win_rate_text)
        tvTopWinRateName = view.findViewById(R.id.captain_details_top_win_rate_name_text)

        ivTopPlayed = view.findViewById(R.id.captain_details_top_played_icon)
        tvTopPlayed = view.findViewById(R.id.captain_details_top_played_text)
        tvTopPlayedName = view.findViewById(R.id.captain_details_top_played_name_text)

        ivTopAvgDmg = view.findViewById(R.id.captain_details_top_average_dmg_icon)
        tvTopAvgDmg = view.findViewById(R.id.captain_details_top_average_dmg_text)
        tvTopAvgDmgName = view.findViewById(R.id.captain_details_top_average_dmg_name_text)

        ivTopTotalKills = view.findViewById(R.id.captain_details_top_total_kill_icon)
        tvTopTotalKills = view.findViewById(R.id.captain_details_top_total_kill_text)
        tvTopTotalKillsName = view.findViewById(R.id.captain_details_top_total_kill_name_text)

        ivTopDistance = view.findViewById(R.id.captain_details_top_distance_traveled_icon)
        tvTopDistance = view.findViewById(R.id.captain_details_top_distance_traveled_text)
        tvTopDistanceName = view.findViewById(R.id.captain_details_top_distance_traveled_name_text)

        ivTopTotalDmg = view.findViewById(R.id.captain_details_top_total_damage_icon)
        tvTopTotalDmg = view.findViewById(R.id.captain_details_top_total_damage_text)
        tvTopTotalDmgName = view.findViewById(R.id.captain_details_top_total_damage_name_text)

        ivTopTotalExp = view.findViewById(R.id.captain_details_top_total_exp_icon)
        tvTopTotalExp = view.findViewById(R.id.captain_details_top_total_exp_text)
        tvTopTotalExpName = view.findViewById(R.id.captain_details_top_total_exp_name_text)

        ivTopTotalPlanes = view.findViewById(R.id.captain_details_top_total_planes_icon)
        tvTopTotalPlanes = view.findViewById(R.id.captain_details_top_total_planes_text)
        tvTopTotalPlanesName = view.findViewById(R.id.captain_details_top_total_planes_name_text)

        ivTopSurvivedWins = view.findViewById(R.id.captain_details_top_survived_wins_icon)
        tvTopSurvivedWins = view.findViewById(R.id.captain_details_top_survived_wins_text)
        tvTopSurvivedWinsName = view.findViewById(R.id.captain_details_top_survived_wins_name_text)

        ivTopCARating = view.findViewById(R.id.captain_details_top_ca_rating_icon)
        tvTopCaRating = view.findViewById(R.id.captain_details_top_ca_rating_text)
        tvTopCARatingName = view.findViewById(R.id.captain_details_top_ca_rating_name_text)

        ivTopMBAccuracy = view.findViewById(R.id.captain_details_top_mb_acc_icon)
        tvTopMBAccuracy = view.findViewById(R.id.captain_details_top_mb_acc_text)
        tvTopMBAccuracyName = view.findViewById(R.id.captain_details_top_mb_acc_name_text)

        ivTopTBAccuracy = view.findViewById(R.id.captain_details_top_tb_acc_icon)
        tvTopTBAccuracy = view.findViewById(R.id.captain_details_top_tb_acc_text)
        tvTopTBAccuracyName = view.findViewById(R.id.captain_details_top_tb_acc_name_text)

        ivTopSpotted = view.findViewById(R.id.captain_details_top_spotted_icon)
        tvTopSpotted = view.findViewById(R.id.captain_details_top_spotted_text)
        tvTopSpottedName = view.findViewById(R.id.captain_details_top_spotted_name_text)

        ivTopScoutingDamage = view.findViewById(R.id.captain_details_top_spotting_damage_icon)
        tvTopScoutingDamage = view.findViewById(R.id.captain_details_top_spotting_damage_text)
        tvTopScoutingDamageName =
            view.findViewById(R.id.captain_details_top_spotting_damage_name_text)

        ivTopTanking = view.findViewById(R.id.captain_details_top_tanking_icon)
        tvTopTanking = view.findViewById(R.id.captain_details_top_tanking_text)
        tvTopTankingName = view.findViewById(R.id.captain_details_top_tanking_name_text)

        ivTopTorpTanking = view.findViewById(R.id.captain_details_top_torp_tanking_icon)
        tvTopTorpTanking = view.findViewById(R.id.captain_details_top_torp_tanking_text)
        tvTopTorpTankingName = view.findViewById(R.id.captain_details_top_torp_tanking_name_text)

        tvBatteryMainDestroyed = view.findViewById(R.id.captain_details_main_destroyed)
        tvBatteryMainHitRatio = view.findViewById(R.id.captain_details_main_hit_ratio)
        tvBatteryMainShots = view.findViewById(R.id.captain_details_main_shots)
        ivBatteryMain = view.findViewById(R.id.captain_details_main_ship_icon)
        tvBatteryMainShipName = view.findViewById(R.id.captain_details_main_ship_name)
        tvBatteryMainShipNumber = view.findViewById(R.id.captain_details_main_ship_number)

        tvBatterySecondaryDestroyed = view.findViewById(R.id.captain_details_secondary_destroyed)
        tvBatterySecondaryHitRatio = view.findViewById(R.id.captain_details_secondary_hit_ratio)
        tvBatterySecondaryShots = view.findViewById(R.id.captain_details_secondary_shots)
        ivBatterySecondary = view.findViewById(R.id.captain_details_secondary_ship_icon)
        tvBatterySecondaryShipName = view.findViewById(R.id.captain_details_secondary_ship_name)
        tvBatterySecondaryShipNumber = view.findViewById(R.id.captain_details_secondary_ship_number)

        tvBatteryTorpsDestroyed = view.findViewById(R.id.captain_details_torps_destroyed)
        tvBatteryTorpsHitRatio = view.findViewById(R.id.captain_details_torps_hit_ratio)
        tvBatteryTorpsShots = view.findViewById(R.id.captain_details_torps_shots)
        ivBatteryTorps = view.findViewById(R.id.captain_details_torps_ship_icon)
        tvBatteryTorpsShipName = view.findViewById(R.id.captain_details_torps_ship_name)
        tvBatteryTorpsShipNumber = view.findViewById(R.id.captain_details_torps_ship_number)

        tvBatteryAircraftDestroyed = view.findViewById(R.id.captain_details_aircraft_destroyed)
        tvBatteryAircraftHitRatio = view.findViewById(R.id.captain_details_aircraft_hit_ratio)
        tvBatteryAircraftShots = view.findViewById(R.id.captain_details_aircraft_shots)
        ivBatteryAircraft = view.findViewById(R.id.captain_details_aircraft_ship_icon)
        tvBatteryAircraftShipName = view.findViewById(R.id.captain_details_aircraft_ship_name)
        tvBatteryAircraftShipNumber = view.findViewById(R.id.captain_details_aircraft_ship_number)

        tvBatteryRammingDestroyed = view.findViewById(R.id.captain_details_ramming_destroyed)
        tvBatteryRammingHitRatio = view.findViewById(R.id.captain_details_ramming_hit_ratio)
        tvBatteryRammingShots = view.findViewById(R.id.captain_details_ramming_shots)
        ivBatteryRamming = view.findViewById(R.id.captain_details_ramming_ship_icon)
        tvBatteryRammingShipName = view.findViewById(R.id.captain_details_ramming_ship_name)
        tvBatteryRammingShipNumber = view.findViewById(R.id.captain_details_ramming_ship_number)

        setUpCard(view, R.id.captain_details_top_damage_area)
        setUpCard(view, R.id.captain_details_top_kills_area)
        setUpCard(view, R.id.captain_details_top_exp_area)
        setUpCard(view, R.id.captain_details_top_planes_area)


        setUpCard(view, R.id.captain_details_top_average_dmg_area)
        setUpCard(view, R.id.captain_details_top_total_kill_area)
        setUpCard(view, R.id.captain_details_top_survival_rate_area)
        setUpCard(view, R.id.captain_details_top_win_rate_area)
        setUpCard(view, R.id.captain_details_top_played_area)
        setUpCard(view, R.id.captain_details_top_distance_traveled_area)
        setUpCard(view, R.id.captain_details_top_ca_rating_area)
        setUpCard(view, R.id.captain_details_top_mb_acc_area)
        setUpCard(view, R.id.captain_details_top_tb_acc_area)

        setUpCard(view, R.id.captain_details_top_spotted_area)
        setUpCard(view, R.id.captain_details_top_spotting_damage_area)
        setUpCard(view, R.id.captain_details_top_tanking_area)
        setUpCard(view, R.id.captain_details_top_torp_tanking_area)

        setUpCard(view, R.id.captain_details_top_total_damage_area)
        setUpCard(view, R.id.captain_details_top_total_exp_area)
        setUpCard(view, R.id.captain_details_top_total_planes_area)
        setUpCard(view, R.id.captain_details_top_survived_wins_area)

        setUpCard(view, R.id.captain_details_main_battery_area)
        setUpCard(view, R.id.captain_details_secondary_area)
        setUpCard(view, R.id.captain_details_torps_area)
        setUpCard(view, R.id.captain_details_aircraft_area)
        setUpCard(view, R.id.captain_details_ramming_area)

        bindSwipe(view)
        initSwipeLayout()
    }

    override fun onResume() {
        super.onResume()
        eventBus.register(this)
        initView()
    }

    private fun initView() {
        var captain: Captain? = null
        try {
            captain = (getActivity() as ICaptain?)!!.getCaptain(getContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if ((captain != null) && (captain.getDetails() != null) && (captain.getDetails()
                .getBattles() > 0)
        ) {
            refreshing(false)

            //armament used
            val mainBatteryStats: BatteryStats = captain.getDetails().getMainBattery()
            val torpStats: BatteryStats = captain.getDetails().getTorpedoes()
            val aircraftStats: BatteryStats = captain.getDetails().getAircraft()

            tvArmamentMain!!.setText("" + mainBatteryStats.frags)
            tvArmamentTorps!!.setText("" + torpStats.frags)
            tvArmamentAircraft!!.setText("" + aircraftStats.frags)
            val others: Int = captain.getDetails()
                .getFrags() - mainBatteryStats.frags - torpStats.frags - aircraftStats.frags
            tvArmamentOthers!!.setText("" + others)
            setUpTopArea(captain)
            setUpBatteryInfo(captain)
        }
    }

    private fun setUpTopArea(captain: Captain) {
        val highDefImage: Boolean =
            tvTopKillsName!!.getContext().getResources().getBoolean(R.bool.high_def_images)

        //kills
        val topKillsShip: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxFragsInBattleShipId())
        setTopShipImageName(topKillsShip, tvTopKillsName, ivTopKills, highDefImage)
        tvTopKills!!.setText("" + captain.getDetails().getMaxFragsInBattle())

        //damage
        val topDamageShip: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxDamageShipId())
        setTopShipImageName(topDamageShip, tvTopDamageName, ivTopDamage, highDefImage)
        tvTopDamage!!.setText("" + captain.getDetails().getMaxDamage())

        //xp
        val topXPShip: ShipInfo? =
            infoManager!!.getShipInfo(requireContext()).get(captain.getDetails().getMaxXPShipId())
        setTopShipImageName(topXPShip, tvTopXPName, ivTopXP, highDefImage)
        tvTopXP!!.setText("" + captain.getDetails().getMaxXP())

        //planes
        val topPlanesShip: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxPlanesKilledShipId())
        setTopShipImageName(topPlanesShip, tvTopPlanesName, ivTopPlanes, highDefImage)
        tvTopPlanes!!.setText("" + captain.getDetails().getMaxPlanesKilled())

        // survival Rate
        val topSurvivalRate: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxSurvivalRateShipId())
        setTopShipImageName(topSurvivalRate, tvTopSurvivalRateName, ivTopSurvivalRate, highDefImage)
        tvTopSurvivalRate!!.setText(
            oneDepthDecimalFormatter.format(
                (captain.getDetails().getMaxSurvivalRate() * 100).toDouble()
            ) + "%"
        )

        // win rate
        val topWinRate: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxWinRateShipId())
        setTopShipImageName(topWinRate, tvTopWinRateName, ivTopWinRate, highDefImage)
        tvTopWinRate!!.setText(
            oneDepthDecimalFormatter.format(
                (captain.getDetails().getMaxWinRate() * 100).toDouble()
            ) + "%"
        )

        // average dmg
        val topAvgDmg: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxAvgDmgShipId())
        setTopShipImageName(topAvgDmg, tvTopAvgDmgName, ivTopAvgDmg, highDefImage)
        tvTopAvgDmg!!.setText((captain.getDetails().getMaxAvgDmg().toInt()).toString() + "")

        // total kills
        val topKills: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTotalKillsShipId())
        setTopShipImageName(topKills, tvTopTotalKillsName, ivTopTotalKills, highDefImage)
        tvTopTotalKills!!.setText(captain.getDetails().getMaxTotalKills().toString() + "")

        // total battles
        val topPlayed: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxPlayedShipId())
        setTopShipImageName(topPlayed, tvTopPlayedName, ivTopPlayed, highDefImage)
        tvTopPlayed!!.setText(captain.getDetails().getMaxPlayed().toString() + "")

        // total planes
        val topPlanes: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTotalPlanesShipId())
        setTopShipImageName(topPlanes, tvTopTotalPlanesName, ivTopTotalPlanes, highDefImage)
        tvTopTotalPlanes!!.setText(
            (captain.getDetails().getMaxTotalPlanes().toInt()).toString() + ""
        )

        // total damage
        val topDamage: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTotalDmgShipId())
        setTopShipImageName(topDamage, tvTopTotalDmgName, ivTopTotalDmg, highDefImage)
        tvTopTotalDmg!!.setText((captain.getDetails().getMaxTotalDamage().toInt()).toString() + "")

        // total exp
        val topXp: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTotalExpShipId())
        setTopShipImageName(topXp, tvTopTotalExpName, ivTopTotalExp, highDefImage)
        tvTopTotalExp!!.setText((captain.getDetails().getMaxTotalExp().toInt()).toString() + "")

        // total exp
        val topSurvivedWins: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxSurvivedWinsShipId())
        setTopShipImageName(topSurvivedWins, tvTopSurvivedWinsName, ivTopSurvivedWins, highDefImage)
        tvTopSurvivedWins!!.setText(
            oneDepthDecimalFormatter.format(
                (captain.getDetails().getMaxSurvivedWins() * 100).toDouble()
            ) + "%"
        )

        // distance
        val topDistanceTraveled: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxMostTraveledShipId())
        setTopShipImageName(topDistanceTraveled, tvTopDistanceName, ivTopDistance, highDefImage)
        val kilos: Float = captain.getDetails().getMaxMostTraveled() * 1.60934f //kilos
        val format: DecimalFormat = DecimalFormat("###,###,###")
        tvTopDistance!!.setText(format.format(kilos.toDouble()) + "km")

        // total ca
        val topCARating: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxCARatingShipId())
        setTopShipImageName(topCARating, tvTopCARatingName, ivTopCARating, highDefImage)
        tvTopCaRating!!.setText((captain.getDetails().getMaxCARating().toInt()).toString() + "")

        // total mb accuracy
        val topMBAccRating: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxMBAccuracyShipId())
        setTopShipImageName(topMBAccRating, tvTopMBAccuracyName, ivTopMBAccuracy, highDefImage)
        tvTopMBAccuracy!!.setText(
            oneDepthDecimalFormatter.format(
                (captain.getDetails().getMaxMBAccuracy() * 100).toDouble()
            ) + "%"
        )

        // total tb accuracy
        val topTBAccRating: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTBAccuracyShipId())
        setTopShipImageName(topTBAccRating, tvTopTBAccuracyName, ivTopTBAccuracy, highDefImage)
        tvTopTBAccuracy!!.setText(
            oneDepthDecimalFormatter.format(
                (captain.getDetails().getMaxTBAccuracy() * 100).toDouble()
            ) + "%"
        )

        val topSpottingRating: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxSpottedShipId())
        setTopShipImageName(topSpottingRating, tvTopSpottedName, ivTopSpotted, highDefImage)
        tvTopSpotted!!.setText(
            oneDepthDecimalFormatter.format(
                captain.getDetails().getMaxSpotted()
            )
        )

        val topSpottingDamage: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxDamageScoutingShipId())
        setTopShipImageName(
            topSpottingDamage,
            tvTopScoutingDamageName,
            ivTopScoutingDamage,
            highDefImage
        )
        tvTopScoutingDamage!!.setText(
            oneDepthDecimalFormatter.format(
                captain.getDetails().getMaxDamageScouting()
            )
        )

        val topTanking: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxArgoDamageShipId())
        setTopShipImageName(topTanking, tvTopTankingName, ivTopTanking, highDefImage)
        tvTopTanking!!.setText(
            oneDepthDecimalFormatter.format(
                captain.getDetails().getMaxTotalArgo()
            )
        )

        d("CaptainTopShip", "torp = " + captain.getDetails().getMaxTorpArgoDamageShipId())
        val topTropArgo: ShipInfo? = infoManager!!.getShipInfo(requireContext())
            .get(captain.getDetails().getMaxTorpArgoDamageShipId())
        setTopShipImageName(topTropArgo, tvTopTorpTankingName, ivTopTorpTanking, highDefImage)
        tvTopTorpTanking!!.setText(
            oneDepthDecimalFormatter.format(
                captain.getDetails().getMaxTorpTotalArgo()
            )
        )
    }

    private fun setTopShipImageName(
        shipInfo: ShipInfo?,
        shipName: TextView?,
        image: ImageView?,
        highDef: Boolean
    ) {
        if (shipInfo != null) {
            setShipImage((image)!!, shipInfo)
            shipName!!.setText(shipInfo.getName())
        }
    }

    private fun setUpBatteryInfo(captain: Captain) {
        //main battery
        setBatteryStatistics(
            captain.getDetails().getMainBattery(), tvBatteryMainDestroyed, tvBatteryMainHitRatio,
            tvBatteryMainShots, ivBatteryMain, tvBatteryMainShipName, tvBatteryMainShipNumber
        )

        //secondaries
        setBatteryStatistics(
            captain.getDetails().getSecondaryBattery(),
            tvBatterySecondaryDestroyed,
            tvBatterySecondaryHitRatio,
            tvBatterySecondaryShots,
            ivBatterySecondary,
            tvBatterySecondaryShipName,
            tvBatterySecondaryShipNumber
        )

        //torpedoes
        setBatteryStatistics(
            captain.getDetails().getTorpedoes(), tvBatteryTorpsDestroyed, tvBatteryTorpsHitRatio,
            tvBatteryTorpsShots, ivBatteryTorps, tvBatteryTorpsShipName, tvBatteryTorpsShipNumber
        )

        //aircraft
        setBatteryStatistics(
            captain.getDetails().getAircraft(),
            tvBatteryAircraftDestroyed,
            tvBatteryAircraftHitRatio,
            tvBatteryAircraftShots,
            ivBatteryAircraft,
            tvBatteryAircraftShipName,
            tvBatteryAircraftShipNumber
        )

        //ramming
        setBatteryStatistics(
            captain.getDetails().getRamming(),
            tvBatteryRammingDestroyed,
            tvBatteryRammingHitRatio,
            tvBatteryRammingShots,
            ivBatteryRamming,
            tvBatteryRammingShipName,
            tvBatteryRammingShipNumber
        )
    }

    private fun setBatteryStatistics(
        batteryStats: BatteryStats,
        destroyed: TextView?,
        hitratio: TextView?,
        shots: TextView?,
        imageView: ImageView?,
        shipName: TextView?,
        shipNumber: TextView?
    ) {
        destroyed!!.setText("" + batteryStats.frags)
        var hitRatio: Float = 0f
        if (batteryStats.shots != 0) {
            hitRatio = (batteryStats.hits / batteryStats.shots.toFloat()) * 100
        }
        hitratio!!.setText(oneDepthDecimalFormatter.format(hitRatio.toDouble()) + "%")
        shots!!.setText("" + batteryStats.shots)
        shipNumber!!.setText("" + batteryStats.maxFrags)
        val ship: ShipInfo? =
            infoManager!!.getShipInfo(requireContext()).get(batteryStats.maxFragsShipId)
        if (ship != null) {
            setShipImage((imageView)!!, ship)
            shipName!!.setText(ship.getName())
        } else {
            imageView!!.setImageResource(R.color.transparent)
            shipNumber.setText("")
        }
    }

    private fun removeBatteryStatistics(
        destroyed: TextView?,
        hitratio: TextView?,
        shots: TextView?,
        imageView: ImageView?,
        shipName: TextView?,
        shipNumber: TextView?
    ) {
        destroyed!!.setText("")
        hitratio!!.setText("0%")
        shots!!.setText("")
        shipNumber!!.setText("")
        imageView!!.setImageResource(R.color.transparent)
        shipNumber.setText("")
    }

    override fun onPause() {
        super.onPause()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onReceive(event: CaptainReceivedEvent?) {
        initView()
    }

    @Subscribe
    fun onRefresh(event: RefreshEvent?) {
        refreshing(true)

        tvArmamentMain!!.setText("")
        tvArmamentTorps!!.setText("")
        tvArmamentAircraft!!.setText("")
        tvArmamentOthers!!.setText("")

        clearTopInfo(ivTopKills, tvTopKillsName, tvTopKills)
        clearTopInfo(ivTopDamage, tvTopDamageName, tvTopDamage)
        clearTopInfo(ivTopXP, tvTopXPName, tvTopXP)
        clearTopInfo(ivTopPlanes, tvTopPlanes, tvTopPlanesName)
        clearTopInfo(ivTopSurvivalRate, tvTopSurvivalRate, tvTopSurvivalRateName)
        clearTopInfo(ivTopWinRate, tvTopWinRate, tvTopWinRateName)
        clearTopInfo(ivTopPlayed, tvTopPlayed, tvTopPlayedName)
        clearTopInfo(ivTopAvgDmg, tvTopAvgDmg, tvTopAvgDmgName)
        clearTopInfo(ivTopTotalKills, tvTopTotalKills, tvTopTotalKillsName)
        clearTopInfo(ivTopDistance, tvTopDistance, tvTopDistanceName)
        clearTopInfo(ivTopTotalDmg, tvTopTotalDmg, tvTopTotalDmgName)
        clearTopInfo(ivTopTotalExp, tvTopTotalExp, tvTopTotalExpName)
        clearTopInfo(ivTopTotalPlanes, tvTopTotalPlanes, tvTopTotalPlanesName)
        clearTopInfo(ivTopSurvivedWins, tvTopSurvivedWins, tvTopSurvivedWinsName)
        clearTopInfo(ivTopCARating, tvTopCaRating, tvTopCARatingName)
        clearTopInfo(ivTopMBAccuracy, tvTopMBAccuracy, tvTopMBAccuracyName)
        clearTopInfo(ivTopTBAccuracy, tvTopTBAccuracy, tvTopTBAccuracyName)

        clearTopInfo(ivTopSpotted, tvTopSpotted, tvTopSpottedName)
        clearTopInfo(ivTopScoutingDamage, tvTopScoutingDamage, tvTopScoutingDamageName)
        clearTopInfo(ivTopTanking, tvTopTanking, tvTopTankingName)
        clearTopInfo(ivTopTorpTanking, tvTopTorpTanking, tvTopTorpTankingName)

        //main battery
        removeBatteryStatistics(
            tvBatteryMainDestroyed, tvBatteryMainHitRatio,
            tvBatteryMainShots, ivBatteryMain, tvBatteryMainShipName, tvBatteryMainShipNumber
        )

        //secondaries
        removeBatteryStatistics(
            tvBatterySecondaryDestroyed,
            tvBatterySecondaryHitRatio,
            tvBatterySecondaryShots,
            ivBatterySecondary,
            tvBatterySecondaryShipName,
            tvBatterySecondaryShipNumber
        )

        //torpedoes
        removeBatteryStatistics(
            tvBatteryTorpsDestroyed, tvBatteryTorpsHitRatio,
            tvBatteryTorpsShots, ivBatteryTorps, tvBatteryTorpsShipName, tvBatteryTorpsShipNumber
        )

        //aircraft
        removeBatteryStatistics(
            tvBatteryAircraftDestroyed,
            tvBatteryAircraftHitRatio,
            tvBatteryAircraftShots,
            ivBatteryAircraft,
            tvBatteryAircraftShipName,
            tvBatteryAircraftShipNumber
        )

        //ramming
        removeBatteryStatistics(
            tvBatteryRammingDestroyed,
            tvBatteryRammingHitRatio,
            tvBatteryRammingShots,
            ivBatteryRamming,
            tvBatteryRammingShipName,
            tvBatteryRammingShipNumber
        )
    }

    private fun clearTopInfo(iv: ImageView?, tv: TextView?, tv1: TextView?) {
        iv!!.setImageResource(R.color.transparent)
        tv!!.setText("")
        tv1!!.setText("")
    }

    @Subscribe
    fun onProgressEvent(event: ProgressEvent) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout!!.setRefreshing(event.isRefreshing)
        }
    }
}