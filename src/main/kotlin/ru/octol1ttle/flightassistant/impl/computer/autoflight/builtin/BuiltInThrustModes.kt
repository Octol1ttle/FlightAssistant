package ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin

import kotlin.math.abs
import kotlin.math.pow
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

data class TakeoffThrustMode(val data: FlightPlanComputer.DepartureData) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            data.takeoffThrust,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.takeoff")
        )
    }
}

data class SpeedThrustMode(val target: Int) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        // TODO: runs every frame but velocity changes only every tick
        val currentThrust: Float = computers.thrust.current
        val currentSpeed: Double = computers.data.forwardVelocity.length() * 20
        val acceleration: Double = computers.data.forwardAcceleration * 20

        val speedCorrection: Double = (target - currentSpeed) * FATickCounter.timePassed.pow(1.5f)
        val accelerationDamping: Double = -acceleration * FATickCounter.timePassed
        return ControlInput(
            (currentThrust + speedCorrection + accelerationDamping).toFloat().coerceIn(0.0f..1.0f),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.speed")
        )
    }
}

data class VerticalProfileThrustMode(val climbThrust: Float, val descendThrust: Float) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        val verticalMode: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if (verticalMode !is SelectedAltitudeVerticalMode) {
            return null
        }
        val nearTarget: Boolean = abs(verticalMode.target - computers.data.altitude) <= 5.0f
        val useClimbThrust: Boolean = nearTarget || verticalMode.target > computers.data.altitude
        return ControlInput(
            if (useClimbThrust) climbThrust else descendThrust,
            ControlInput.Priority.NORMAL,
            if (useClimbThrust) Component.translatable("mode.flightassistant.thrust.climb") else
                if (descendThrust != 0.0f) Component.translatable("mode.flightassistant.thrust.descend")
                else Component.translatable("mode.flightassistant.thrust.idle")
        )
    }
}