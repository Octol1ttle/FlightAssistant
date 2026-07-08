package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics

fun interface FixedGuiRenderCallback {
    /**
     * Called when the main HUD is being rendered.
     */
    fun onRenderGui(context: FAGuiGraphics, partialTick: Float)

    companion object {
        @JvmField
        val EVENT: Event<FixedGuiRenderCallback> = EventFactory.createLoop()
    }
}
