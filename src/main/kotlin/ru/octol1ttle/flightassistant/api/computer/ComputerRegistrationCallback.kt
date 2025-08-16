package ru.octol1ttle.flightassistant.api.computer

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import java.util.function.BiConsumer
import net.minecraft.resources.ResourceLocation

fun interface ComputerRegistrationCallback {
    /**
     * Called when the client has started, after all built-in computers have been initialized.
     * Register your custom computers in this event using the provided function
     */
    fun register(computers: ComputerBus, registerFunction: BiConsumer<ResourceLocation, Computer>)

    companion object {
        @JvmField
        val EVENT: Event<ComputerRegistrationCallback> = EventFactory.createLoop()
    }
}
