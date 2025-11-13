package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import net.minecraft.network.chat.Component
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.getProgressOnTrack
import ru.octol1ttle.flightassistant.api.util.extensions.vec2dFromInts
import ru.octol1ttle.flightassistant.api.util.pointsToDirection
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class HeadingLateralMode(override val targetHeading: Int, override val textOverride: Component? = null) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsHeadingMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            targetHeading.toFloat(),
            Component.translatable("mode.flightassistant.lateral.heading")
        )
    }
}

data class DirectCoordinatesLateralMode(override val targetX: Int, override val targetZ: Int, override val textOverride: Component? = null) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsCoordinatesMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            pointsToDirection(targetX.toDouble(), targetZ.toDouble(), computers.data.x, computers.data.z).toFloat() + 180.0f,
            Component.translatable("mode.flightassistant.lateral.direct_coordinates")
        )
    }
}

data class TrackNavigationLateralMode(val originX: Int, val originZ: Int, override val targetX: Int, override val targetZ: Int, override val textOverride: Component? = null) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsCoordinatesMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val targetCoordinates: Vector2d = getTargetCoordinates(computers)
        return ControlInput(
            pointsToDirection(targetCoordinates.x, targetCoordinates.y, computers.data.x, computers.data.z).toFloat() + 180.0f,
            Component.translatable("mode.flightassistant.lateral.track_navigation")
        )
    }

    private fun getTargetCoordinates(computers: ComputerBus): Vector2d {
        val origin: Vector2d = vec2dFromInts(originX, originZ)
        val track: Vector2d = vec2dFromInts(targetX, targetZ).sub(origin)
        val trackProgress: Double = getProgressOnTrack(track, origin, Vector2d(computers.data.x, computers.data.z))
        val closestTrackPoint = Vector2d(originX + trackProgress * track.x, originZ + trackProgress * track.y)
        val trackNormalized: Vector2d = track.normalize()
        return closestTrackPoint.add(trackNormalized.mul(computers.data.velocityPerSecond.horizontalDistance() * 3.0))
    }
}