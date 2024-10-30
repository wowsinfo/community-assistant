package com.half.wowsca.managers

import com.half.wowsca.model.Ship
import com.half.wowsca.model.encyclopedia.items.ShipStat

/**
 * Created by slai4 on 1/17/2016.
 */
object CARatingManager {
    const val DAMAGE_COEF: Float = 0.50f
    const val KILLS_COEF: Float = 0.30f
    const val WR_COEF: Float = 0.20f
    const val ONE: Float = 1f
    private const val NORMALIZE_VALUE = 1000

    /**
     * Creates a rating based off of performance on a ship
     *
     *
     * You must check if there are no battles before calling this method.
     *
     * @param ship
     * @param info
     * @return
     */
    @JvmStatic
    fun CalculateCAShipRating(ship: Ship, info: ShipStat): Float {
        var rating = 0f
        val battles = ship.battles.toFloat()

        //Calculate c's by total / battles
        val cDmg = (ship.totalDamage / battles).toFloat()
        val cWin = ship.wins / battles
        val cKills = ship.frags / battles

        var xDmg = ONE
        if (info.dmg_dlt > 0) xDmg = (cDmg / info.dmg_dlt) // c / e damage

        var xWR = ONE
        if (info.wins > 0) xWR = (cWin / info.wins) // c / e wins

        var xKills = ONE
        if (info.frags > 0) xKills = (cKills / info.frags) // c / e kills


        //        Dlog.d("CalculateShipRating", "dmg = " + xDmg + " wr = " + xWR + " kills = " + xKills);

        //Add up portions of the total
        val totalPortions =
            (xDmg * DAMAGE_COEF) + (xKills * KILLS_COEF) + (xWR * WR_COEF) // (c/e) * portion

        rating = totalPortions * NORMALIZE_VALUE // normalize 1000

        //        Dlog.d("CalculateShipRating", "id = " + ship.getShipId() + " rating = " + rating);
//        Dlog.d("CalculateShipRating", "tRating = " + rating);
        return rating
    }
}
