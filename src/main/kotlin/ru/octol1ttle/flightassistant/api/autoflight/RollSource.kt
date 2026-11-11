package ru.octol1ttle.flightassistant.api.autoflight

/**
 * Defines a source of roll to be used by the [ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer]
 */
interface RollSource {
    fun getRoll(): Float
    fun addRoll(diff: Float)
}
