package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.world.phys.Vec3

fun AbstractClientPlayer.getLerpedDeltaMovement(partialTick: Float): Vec3 {
//? if >=1.21.9 {
    /*return this.avatarState().deltaMovementOnPreviousTick().lerp(this.deltaMovement, partialTick.toDouble())
*///?} else
    return this.getDeltaMovementLerped(partialTick)
}