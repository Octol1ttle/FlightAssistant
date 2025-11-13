package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import ru.octol1ttle.flightassistant.mixin.EntityInvoker

fun BlockState.notClimbable(entity: Entity): Boolean {
    return isAir || !(entity as EntityInvoker).invokeIsStateClimbable(this)
}