package ru.octol1ttle.flightassistant.api.autoflight

import net.minecraft.network.chat.Component

/**
 * Represents a flight controls input.
 *
 * @param target The target value of this input (e.g. the target thrust)
 * @param text The text that will be shown on the [ru.octol1ttle.flightassistant.impl.display.AutomationModesDisplay] when this input is being satisfied
 * @param priority The priority of this input
 * @param deltaTimeMultiplier The multiplier for the delta time, used to determine how fast an input will be satisfied
 * @param status The status of this input
 */
data class ControlInput(val target: Float, val text: Component? = null, val priority: Priority = Priority.NORMAL, val deltaTimeMultiplier: Float = 1.0f, val status: Status = Status.ACTIVE) {
    enum class Priority(val value: Int) {
        HIGHEST(0),
        HIGH(1),
        NORMAL(2),
        LOW(3),
        LOWEST(4);

        fun isHigherOrSame(other: Priority?): Boolean {
            if (other == null) {
                return true
            }

            return this.value <= other.value
        }
    }

    enum class Status {
        ACTIVE,
        ARMED,
        UNAVAILABLE,
        DISABLED;

        companion object {
            fun highest(first: Status?, second: Status?): Status {
                if (first == null) return second ?: DISABLED
                if (second == null) return first
                return if (first <= second) first else second
            }

            fun fromBooleans(active: Boolean, available: Boolean = true, enabled: Boolean = true): Status {
                return if (!enabled) DISABLED
                else if (!available) UNAVAILABLE
                else if (!active) ARMED else ACTIVE
            }
        }
    }
}
