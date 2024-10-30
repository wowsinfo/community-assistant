package com.half.wowsca.model

import com.half.wowsca.model.encyclopedia.holders.ShipsHolder

/**
 * Created by slai4 on 9/26/2015.
 */
class ShipCompare {
    @JvmField
    var battlesComparator: Comparator<Ship> = Comparator { lhs, rhs -> rhs.battles - lhs.battles }
    @JvmField
    var averageExpComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsBattles = rhs.battles
        val lhsBattles = lhs.battles
        val rhsAverage = (rhs.totalXP / rhsBattles).toInt()
        val lhsAverage = (lhs.totalXP / lhsBattles).toInt()
        rhsAverage - lhsAverage
    }
    @JvmField
    var averageDamageComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsBattles = rhs.battles
        val lhsBattles = lhs.battles
        val rhsAverage = (rhs.totalDamage / rhsBattles).toInt()
        val lhsAverage = (lhs.totalDamage / lhsBattles).toInt()
        rhsAverage - lhsAverage
    }
    @JvmField
    var winRateComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsBattles = rhs.battles.toFloat()
        val lhsBattles = lhs.battles.toFloat()
        val rhsAverage = ((rhs.wins / rhsBattles) * 100).toInt()
        val lhsAverage = ((lhs.wins / lhsBattles) * 100).toInt()
        rhsAverage - lhsAverage
    }
    @JvmField
    var killsComparator: Comparator<Ship> = Comparator { lhs, rhs -> rhs.frags - lhs.frags }
    @JvmField
    var killsDeathComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        var rhsBattles = rhs.battles - rhs.survivedBattles.toFloat()
        if (rhsBattles <= 1) rhsBattles = 1f
        var lhsBattles = lhs.battles - lhs.survivedBattles.toFloat()
        if (lhsBattles <= 1) lhsBattles = 1f
        val rhsFrags = rhs.frags.toFloat() / rhsBattles
        val lhsFrags = lhs.frags.toFloat() / lhsBattles
        if (rhsFrags > lhsFrags) {
            1
        } else if (rhsFrags < lhsFrags) {
            -1
        } else {
            0
        }
    }
    @JvmField
    var accuracyComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsShots = rhs.mainBattery.shots.toFloat()
        val lhsShots = lhs.mainBattery.shots.toFloat()
        var rhsAcc = 0f
        if (rhsShots > 0) {
            rhsAcc = rhs.mainBattery.hits.toFloat() / rhsShots
        }
        var lhsAcc = 0f
        if (lhsShots > 0) {
            lhsAcc = lhs.mainBattery.hits.toFloat() / lhsShots
        }
        if (rhsAcc > lhsAcc) {
            1
        } else if (rhsAcc < lhsAcc) {
            -1
        } else {
            0
        }
    }
    @JvmField
    var accuractTorpsComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsShots = rhs.torpedoes.shots.toFloat()
        val lhsShots = lhs.torpedoes.shots.toFloat()
        var rhsAcc = -1f
        if (rhsShots > 0) {
            rhsAcc = rhs.torpedoes.hits.toFloat() / rhsShots
        }
        var lhsAcc = -1f
        if (lhsShots > 0) {
            lhsAcc = lhs.torpedoes.hits.toFloat() / lhsShots
        }
        if (rhsAcc > lhsAcc) {
            1
        } else if (rhsAcc < lhsAcc) {
            -1
        } else {
            0
        }
    }
    @JvmField
    var planeKillsComparator: Comparator<Ship> =
        Comparator { lhs, rhs -> rhs.planesKilled - lhs.planesKilled }
    @JvmField
    var damageComparator: Comparator<Ship> =
        Comparator { lhs, rhs -> (rhs.totalDamage - lhs.totalDamage).toInt() }
    @JvmField
    var CARatingComparator: Comparator<Ship> =
        Comparator { lhs, rhs -> (rhs.caRating - lhs.caRating).toInt() }
    @JvmField
    var shipsHolder: ShipsHolder? = null
    @JvmField
    var namesComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsInfo = shipsHolder!![rhs.shipId]
        val lhsInfo = shipsHolder!![lhs.shipId]
        var rhsName: String? = ""
        var lhsName = ""
        if (rhsInfo != null) rhsName = rhsInfo.name
        if (lhsInfo != null) lhsName = lhsInfo.name
        lhsName.compareTo(rhsName!!, ignoreCase = true)
    }
    @JvmField
    var tierDescendingComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsInfo = shipsHolder!![rhs.shipId]
        val lhsInfo = shipsHolder!![lhs.shipId]
        var rhsT = 0
        var lhsT = 0
        if (rhsInfo != null) rhsT = rhsInfo.tier
        if (lhsInfo != null) lhsT = lhsInfo.tier
        if (rhsT > lhsT) {
            1
        } else if (rhsT < lhsT) {
            -1
        } else {
            0
        }
    }

    @JvmField
    var tierAscendingComparator: Comparator<Ship> = Comparator { lhs, rhs ->
        val rhsInfo = shipsHolder!![rhs.shipId]
        val lhsInfo = shipsHolder!![lhs.shipId]
        var rhsT = 0
        var lhsT = 0
        if (rhsInfo != null) rhsT = rhsInfo.tier
        if (lhsInfo != null) lhsT = lhsInfo.tier
        if (rhsT < lhsT) {
            1
        } else if (rhsT > lhsT) {
            -1
        } else {
            0
        }
    }
}
