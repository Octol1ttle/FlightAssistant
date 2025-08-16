package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.gui.GuiGraphics

fun interface FixedGuiRenderCallback {
    /**
     * Called when the main HUD is being rendered.
     */
    fun onRenderGui(context: GuiGraphics, partialTick: Float)

    companion object {
        @JvmField
        val EVENT: Event<FixedGuiRenderCallback> = EventFactory.createLoop()
    }
}
