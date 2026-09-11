package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
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

class HoldingPatternLateralMode(
    val fixX: Int,
    val fixZ: Int,
    val inboundCourse: Float,
    val legLength: Double = 300.0,
    val turnDirection: TurnDirection = TurnDirection.RIGHT,
    override val textOverride: Component? = null
) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsCoordinatesMode {
    override val targetX: Int get() = fixX
    override val targetZ: Int get() = fixZ

    // The far turn point of the racetrack: offset from the fix along the outbound leg,
    // then offset sideways so alternating direct-to steering traces an oval instead of a straight line.
    private val outboundPoint: Vector2d = run {
        val outboundCourse: Double = inboundCourse + 180.0
        val sideCourse: Double = outboundCourse + 90.0 * turnDirection.sign
        vec2dFromInts(fixX, fixZ)
            .add(directionVector(outboundCourse).mul(legLength))
            .add(directionVector(sideCourse).mul(legLength / 3.0))
    }

    private var flyingOutbound: Boolean = true

    override fun getControlInput(computers: ComputerBus): ControlInput {
        val fix: Vector2d = vec2dFromInts(fixX, fixZ)
        val target: Vector2d = if (flyingOutbound) outboundPoint else fix
        if (target.distance(computers.data.x, computers.data.z) < computers.data.velocityPerSecond.horizontalDistance() * 3.0) {
            flyingOutbound = !flyingOutbound
        }

        return DirectCoordinatesLateralMode(target.x.roundToInt(), target.y.roundToInt()).getControlInput(computers)
            .copy(text = Component.translatable("mode.flightassistant.lateral.holding_pattern"))
    }

    enum class TurnDirection(val sign: Double) {
        LEFT(-1.0), RIGHT(1.0)
    }

    companion object {
        private fun directionVector(courseDegrees: Double): Vector2d {
            val radians: Double = Math.toRadians(courseDegrees)
            return Vector2d(-sin(radians), cos(radians))
        }
    }
}