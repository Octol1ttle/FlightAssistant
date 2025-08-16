package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.projectile.FireworkRocketEntity

fun interface FireworkBoostCallback {
    fun onFireworkBoost(rocket: FireworkRocketEntity?, shooter: LocalPlayer)

    companion object {
        @JvmField
        val EVENT: Event<FireworkBoostCallback> = EventFactory.createLoop()
    }
}
