package ru.octol1ttle.flightassistant.api.util

import net.minecraft.SharedConstants
import net.minecraft.util.Mth

class FloatLerper {
    private var lastValue: Float? = null

    fun get(currentValue: Float?, delta: Float = 1.0f / SharedConstants.TICKS_PER_SECOND): Float? {
        if (currentValue == null || lastValue == null) {
            lastValue = currentValue
            return currentValue
        }
        val result: Float = Mth.lerp(delta, lastValue!!, currentValue)
        lastValue = result
        return result
    }
}