package com.half.wowsca.util

import com.half.wowsca.model.AddRemoveEvent
import com.half.wowsca.model.CaptainReceivedEvent
import com.half.wowsca.model.CaptainSavedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.ShipClickedEvent
import com.half.wowsca.model.result.CaptainResult
import com.half.wowsca.model.result.ShipResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Lightweight SharedFlow-based event bus to replace GreenRobot EventBus.
 */
object AppEventBus {
    private val _refreshEvents = MutableSharedFlow<RefreshEvent>(extraBufferCapacity = 1)
    val refreshEvents: SharedFlow<RefreshEvent> = _refreshEvents.asSharedFlow()

    private val _progressEvents = MutableSharedFlow<ProgressEvent>(extraBufferCapacity = 1)
    val progressEvents: SharedFlow<ProgressEvent> = _progressEvents.asSharedFlow()

    private val _captainSavedEvents = MutableSharedFlow<CaptainSavedEvent>(extraBufferCapacity = 1)
    val captainSavedEvents: SharedFlow<CaptainSavedEvent> = _captainSavedEvents.asSharedFlow()

    private val _captainResults = MutableSharedFlow<CaptainResult>(extraBufferCapacity = 1)
    val captainResults: SharedFlow<CaptainResult> = _captainResults.asSharedFlow()

    private val _shipResults = MutableSharedFlow<ShipResult>(extraBufferCapacity = 1)
    val shipResults: SharedFlow<ShipResult> = _shipResults.asSharedFlow()

    private val _shipIdEvents = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val shipIdEvents: SharedFlow<Long> = _shipIdEvents.asSharedFlow()

    private val _addRemoveEvents = MutableSharedFlow<AddRemoveEvent>(extraBufferCapacity = 1)
    val addRemoveEvents: SharedFlow<AddRemoveEvent> = _addRemoveEvents.asSharedFlow()

    private val _captainReceivedEvents = MutableSharedFlow<CaptainReceivedEvent>(extraBufferCapacity = 1)
    val captainReceivedEvents: SharedFlow<CaptainReceivedEvent> = _captainReceivedEvents.asSharedFlow()

    private val _shipClickedEvents = MutableSharedFlow<ShipClickedEvent>(extraBufferCapacity = 1)
    val shipClickedEvents: SharedFlow<ShipClickedEvent> = _shipClickedEvents.asSharedFlow()

    /**
     * Generic post that routes to the correct SharedFlow based on type.
     */
    fun post(event: Any) {
        when (event) {
            is RefreshEvent -> _refreshEvents.tryEmit(event)
            is ProgressEvent -> _progressEvents.tryEmit(event)
            is CaptainSavedEvent -> _captainSavedEvents.tryEmit(event)
            is CaptainResult -> _captainResults.tryEmit(event)
            is ShipResult -> _shipResults.tryEmit(event)
            is Long -> _shipIdEvents.tryEmit(event)
            is AddRemoveEvent -> _addRemoveEvents.tryEmit(event)
            is CaptainReceivedEvent -> _captainReceivedEvents.tryEmit(event)
            is ShipClickedEvent -> _shipClickedEvents.tryEmit(event)
            else -> { /* unhandled event type */ }
        }
    }

    fun postShipId(id: Long) { _shipIdEvents.tryEmit(id) }
}
