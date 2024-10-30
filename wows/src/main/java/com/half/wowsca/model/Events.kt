package com.half.wowsca.model

/**
 * Moved from the old model package into one file
 */

class AddRemoveCaptainEvent {
    var isRemoved: Boolean = false
}

class AddRemoveEvent {
    var isRemove: Boolean = false
    @JvmField
    var captain: Captain? = null
}

class CaptainReceivedEvent

class CaptainSavedEvent

class CaptainSkillClickedEvent(@JvmField var id: Long)

class FlagClickedEvent(@JvmField var id: Long)

class ProgressEvent(val isRefreshing: Boolean)

class RefreshEvent(var isFromSwipe: Boolean)

class ScrollToEvent {
    @JvmField
    var position: Int = 0
}

class ShipClickedEvent(@JvmField var id: Long)

class ShipCompareEvent(var shipId: Long) {
    var isCleared: Boolean = false
}

class SortingDoneEvent

class UpgradeClickEvent(@JvmField var id: Long)
