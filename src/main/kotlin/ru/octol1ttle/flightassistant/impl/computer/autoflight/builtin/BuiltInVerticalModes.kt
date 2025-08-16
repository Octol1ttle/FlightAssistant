package ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin

import kotlin.math.abs
import kotlin.math.pow
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class PitchVerticalMode(val target: Float) : AutoFlightComputer.VerticalMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        return ControlInput(
            target,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.vertical.pitch")
        )
    }
}

data class SpeedReferenceVerticalMode(val targetSpeed: Int) : AutoFlightComputer.VerticalMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        // TODO: find a way to fix jittering
        val range: Float = computers.thrust.getOptimumClimbPitch() - computers.thrust.getAltitudeHoldPitch()
        val currentPitch: Float = computers.data.pitch
        val currentSpeed: Double = computers.data.forwardVelocity.length() * 20
        val acceleration: Double = computers.data.forwardAcceleration * 20

        val speedCorrection: Double = (currentSpeed - targetSpeed) * FATickCounter.timePassed.pow(1.5f) * range
        val accelerationDamping: Double = acceleration * FATickCounter.timePassed.pow(2.0f) * range
        return ControlInput(
            (currentPitch + speedCorrection + accelerationDamping).toFloat().coerceIn(computers.thrust.getAltitudeHoldPitch()..computers.thrust.getOptimumClimbPitch()),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.vertical.speed_reference")
        )
    }
}

data class SelectedAltitudeVerticalMode(val target: Int) : AutoFlightComputer.VerticalMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        val diff: Float = (target - computers.data.altitude).toFloat()
        val abs: Float = abs(diff)
        val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()

        var finalPitch: Float
        var text: Component
        if (diff >= 0) {
            finalPitch = computers.thrust.getOptimumClimbPitch()
            text = Component.translatable("mode.flightassistant.vertical.altitude.climb")

            val distanceFromNeutral: Float = finalPitch - neutralPitch
            finalPitch -= distanceFromNeutral * 0.6f * ((200.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
            finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
        } else {
            finalPitch = -35.0f
            text = Component.translatable("mode.flightassistant.vertical.altitude.descend")

            val distanceFromNeutral: Float = finalPitch - neutralPitch
            finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
            finalPitch -= distanceFromNeutral * 0.6f * ((50.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
        }

        if (abs <= 5.0f) {
            text = Component.translatable("mode.flightassistant.vertical.altitude.hold")
        }

        return ControlInput(finalPitch, ControlInput.Priority.NORMAL, text, 1.5f)
    }
}