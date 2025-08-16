package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.world.level.Level

val Level.bottomY: Int
    get() {
//? if >=1.21.4 {
        /*return this.minY
*///?} else
        return this.minBuildHeight
    }