package com.half.wowsca.util

import com.half.wowsca.model.CaptainSavedEvent
import com.half.wowsca.model.ProgressEvent
import com.half.wowsca.model.RefreshEvent
import com.half.wowsca.model.result.CaptainResult
import com.half.wowsca.model.result.ShipResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Lightweight SharedFlow-based event bus to replace GreenRobot EventBus.
 * One SharedFlow per event type for type safety.
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

    fun post(event: RefreshEvent) { _refreshEvents.tryEmit(event) }
    fun post(event: ProgressEvent) { _progressEvents.tryEmit(event) }
    fun post(event: CaptainSavedEvent) { _captainSavedEvents.tryEmit(event) }
    fun post(event: CaptainResult) { _captainResults.tryEmit(event) }
    fun post(event: ShipResult) { _shipResults.tryEmit(event) }
}
