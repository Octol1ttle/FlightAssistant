package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.abs
import kotlin.math.pow
import net.minecraft.SharedConstants
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class ConstantThrustMode(val target: Float, override val textOverride: Component) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(target)
    }
}

data class SpeedThrustMode(override val targetSpeed: Int, override val textOverride: Component? = null) : AutoFlightComputer.ThrustMode, AutoFlightComputer.FollowsSpeedMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val currentThrust: Float = computers.thrust.current
        val currentSpeed: Double = computers.data.forwardVelocityPerSecond.length()
        val acceleration: Double = computers.data.forwardAcceleration * SharedConstants.TICKS_PER_SECOND

        val speedCorrection: Double = (targetSpeed - currentSpeed) * FATickCounter.timePassed.pow(1.5f)
        val accelerationDamping: Double = -acceleration * FATickCounter.timePassed
        return ControlInput(
            (currentThrust + speedCorrection + accelerationDamping).toFloat().coerceIn(0.0f..1.0f),
            Component.translatable("mode.flightassistant.thrust.speed")
        )
    }
}

data class VerticalProfileThrustMode(val climbThrust: Float, val descendThrust: Float, override val textOverride: Component? = null) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        val verticalMode: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if (verticalMode !is AutoFlightComputer.FollowsAltitudeMode) {
            return null
        }
        val nearTarget: Boolean = abs(verticalMode.targetAltitude - computers.data.altitude) <= 5.0f
        val useClimbThrust: Boolean = nearTarget || verticalMode.targetAltitude > computers.data.altitude
        return ControlInput(
            if (useClimbThrust) climbThrust else descendThrust,
            if (useClimbThrust) Component.translatable("mode.flightassistant.thrust.climb")
            else if (descendThrust != 0.0f) Component.translatable("mode.flightassistant.thrust.descend")
            else Component.translatable("mode.flightassistant.thrust.idle")
        )
    }
}