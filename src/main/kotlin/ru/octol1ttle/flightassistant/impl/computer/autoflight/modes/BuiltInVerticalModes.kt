package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.abs
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.PIDController
import ru.octol1ttle.flightassistant.api.util.extensions.getProgressOnTrack
import ru.octol1ttle.flightassistant.api.util.extensions.vec2dFromInts
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class PitchVerticalMode(override val targetPitch: Float, override val textOverride: Component? = null) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsPitchMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(targetPitch, Component.translatable("mode.flightassistant.vertical.pitch"))
    }
}

data class VerticalSpeedVerticalMode(val targetVerticalSpeed: Double, override val textOverride: Component? = null) : AutoFlightComputer.VerticalMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val currentVerticalSpeed: Double = computers.data.velocityPerSecond.y
        val target: Float = controller.calculate(targetVerticalSpeed, currentVerticalSpeed, computers.data.pitch.toDouble()).toFloat()

        return ControlInput(target)
    }

    companion object {
        private val controller: PIDController = PIDController(1.8, 0.1, 0.35, 10, -85.0, 85.0)
    }
}

data class SelectedAltitudeVerticalMode(override val targetAltitude: Int, override val textOverride: Component? = null) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val diff: Double = targetAltitude - computers.data.altitude
        val text: Component = if (abs(diff) <= 10.0) {
            Component.translatable("mode.flightassistant.vertical.altitude.hold")
        } else if (diff >= 0) {
            Component.translatable("mode.flightassistant.vertical.altitude.open.climb")
        } else {
            Component.translatable("mode.flightassistant.vertical.altitude.open.descend")
        }

        return VerticalSpeedVerticalMode((targetAltitude - computers.data.altitude) / 2.0).getControlInput(computers).copy(text = text)
    }
}

data class ManagedAltitudeVerticalMode(val originX: Int, val originZ: Int, val originAltitude: Int, val targetX: Int, val targetZ: Int, override val targetAltitude: Int, override val textOverride: Component? = null) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val totalDiff: Double = targetAltitude - computers.data.altitude
        val text: Component =
            if (abs(totalDiff) <= 10.0)
                if (targetAltitude == computers.plan.getCruiseAltitude()) Component.translatable("mode.flightassistant.vertical.altitude.cruise")
                else Component.translatable("mode.flightassistant.vertical.altitude.hold")
            else if (totalDiff > 0) Component.translatable("mode.flightassistant.vertical.altitude.managed.climb")
            else Component.translatable("mode.flightassistant.vertical.altitude.managed.descend")

        val origin: Vector2d = vec2dFromInts(originX, originZ)
        val track: Vector2d = vec2dFromInts(targetX, targetZ).sub(origin)
        val trackProgress: Double = getProgressOnTrack(track, origin, Vector2d(computers.data.x, computers.data.z))
        val trackDiff: Int = targetAltitude - originAltitude

        val timeToTarget: Double = track.length() / computers.data.velocityPerSecond.horizontalDistance()
        val targetVerticalSpeed: Double = trackDiff / timeToTarget

        val currentTarget: Double = Mth.lerp(trackProgress, originAltitude.toDouble(), targetAltitude.toDouble())
        val currentDiff: Double = currentTarget - computers.data.altitude

        return VerticalSpeedVerticalMode(targetVerticalSpeed + currentDiff).getControlInput(computers).copy(text = text)
    }
}