package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.SharedConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.LimitedFIFOQueue
import ru.octol1ttle.flightassistant.api.util.extensions.distance2d
import ru.octol1ttle.flightassistant.api.util.extensions.formatRoot
import ru.octol1ttle.flightassistant.api.util.extensions.getProgressOnTrack
import ru.octol1ttle.flightassistant.api.util.extensions.toVector2d
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.*
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay
import java.time.Duration
import java.util.*
import kotlin.math.roundToLong

class FlightPlanComputer(computers: ComputerBus) : Computer(computers) {
    var currentPhase: FlightPhase = FlightPhase.UNKNOWN
        private set
    var groundSpeeds: LimitedFIFOQueue<Double> = LimitedFIFOQueue(SharedConstants.TICKS_PER_SECOND * 5)
        private set

    var waypoints: MutableList<Waypoint> = ArrayList()
        private set
    var tracking: ActiveSegment? = null

    override fun tick() {
        tryAdvanceTarget()
        currentPhase = updateFlightPhase()
        groundSpeeds.add(computers.data.velocityPerSecond.horizontalDistance())
    }

    private fun updateFlightPhase(): FlightPhase {
        if (waypoints.isEmpty()) {
            return FlightPhase.UNKNOWN
        }

        if (!computers.data.flying) {
            val takeoff = waypoints.singleOrNull { it.phaseMarker == Waypoint.PhaseMarker.TAKEOFF }
            if (takeoff != null && isCloseTo(takeoff)) {
                tracking = ActiveSegment.Leg(takeoff, 1)
                return FlightPhase.TAKEOFF
            }
            return FlightPhase.UNKNOWN
        }

        val target: Waypoint = getEnrouteTarget() ?: return FlightPhase.UNKNOWN
        val targetIndex: Int = waypoints.indexOf(target)

        val approach = waypoints.indexOfFirst { it.phaseMarker == Waypoint.PhaseMarker.APPROACH }
        val landing = waypoints.indexOfFirst { it.phaseMarker == Waypoint.PhaseMarker.LANDING }

        if (approach != -1 && landing != -1) {
            if (targetIndex > landing) {
                return FlightPhase.GO_AROUND
            }
            if ((currentPhase == FlightPhase.APPROACH || currentPhase == FlightPhase.LANDING)
                && computers.thrust.activeInput == null && computers.thrust.current == 1.0f) {
                tracking =
                    if (landing + 1 < waypoints.size) ActiveSegment.Direct(landing + 1)
                    else ActiveSegment.Direct(approach)
                // FIXME: HACK
                computers.thrust.setTarget(0.99f, null)
                return FlightPhase.GO_AROUND
            }
            if (targetIndex == landing) {
                return FlightPhase.LANDING
            }
            if (targetIndex >= approach) {
                return FlightPhase.APPROACH
            }
        }

        val cruiseAltitude: Int = getCruiseAltitude() ?: return FlightPhase.UNKNOWN
        val firstCruise: Int = waypoints.indexOfFirst { it.altitude == cruiseAltitude }
        val lastCruise: Int = waypoints.indexOfLast { it.altitude == cruiseAltitude }

        if (targetIndex <= firstCruise) {
            return FlightPhase.CLIMB
        }
        if (targetIndex <= lastCruise) {
            return FlightPhase.CRUISE
        }

        return FlightPhase.DESCEND
    }

    private fun tryAdvanceTarget() {
        val target: Waypoint = getEnrouteTarget() ?: return

        if (isCloseTo(target)) {
            val targetIndex: Int = waypoints.indexOf(target)
            val last = waypoints.size - 1
            if (targetIndex >= last) {
                val landing = waypoints.indexOfFirst { it.phaseMarker == Waypoint.PhaseMarker.LANDING }
                if (landing != -1 && landing < last) {
                    tracking = ActiveSegment.Leg(target, waypoints.indexOfFirst { it.phaseMarker == Waypoint.PhaseMarker.APPROACH })
                    return
                }

                tracking = null
                return
            }

            tracking = ActiveSegment.Leg(target, targetIndex + 1)
        }
    }

    private fun getEnrouteOrigin(): Waypoint? {
        (tracking as? ActiveSegment.Leg)?.let { return it.origin }
        return null
    }

    private fun getEnrouteTarget(): Waypoint? {
        return when (val tracking = this.tracking) {
            is ActiveSegment.Direct -> waypoints[tracking.index]
            is ActiveSegment.Leg -> waypoints[tracking.targetIndex]
            null -> null
        }
    }

    private fun isCloseTo(target: Waypoint): Boolean {
        val threshold =
            if (target.phaseMarker == Waypoint.PhaseMarker.LANDING) 5.0
            else computers.data.velocityPerSecond.horizontalDistance().coerceAtLeast(10.0)
        return getDistanceToTarget(target) < threshold
    }

    fun getCruiseAltitude(): Int? {
        return waypoints.maxOfOrNull { it.altitude }
    }

    fun getLateralDeviation(position: Vec3): Double? {
        if (waypoints.isEmpty()) {
            return null
        }

        val origin: Vec3 = getEnrouteOrigin()?.vec3() ?: return null
        val target: Vec3 = getEnrouteTarget()!!.vec3()

        val origin2d = origin.toVector2d()
        val track = target.toVector2d().sub(origin2d)
        val toEntity = position.toVector2d().sub(origin2d)
        val cross = track.x * toEntity.y - track.y * toEntity.y
        return cross / track.length()
    }

    fun getVerticalDeviation(position: Vec3): Double? {
        if (waypoints.isEmpty()) {
            return null
        }
        val origin: Vec3 = getEnrouteOrigin()?.vec3() ?: return null
        val target: Vec3 = getEnrouteTarget()!!.vec3()

        val track = target.toVector2d().sub(origin.toVector2d())
        val trackProgress = getProgressOnTrack(track, origin.toVector2d(), Vector2d(position.x, position.z))
        return Mth.lerp(trackProgress, origin.y, target.y) - position.y
    }

    fun getDistanceToTarget(target: Waypoint): Double {
        return distance2d(target.posX, target.posZ, computers.data.x, computers.data.z)
    }

    fun getFormattedTime(distance: Double): String {
        val average = groundSpeeds.average()
        return if (computers.data.flying && average >= 5.0) {
            val duration: Duration = Duration.ofSeconds((distance / average).roundToLong())
            "${duration.toMinutesPart()}:${"%02d".formatRoot(duration.toSecondsPart())}"
        } else
            "--:--"
    }

    fun getThrustMode(): AutoFlightComputer.ThrustMode? {
        val target: Waypoint = getEnrouteTarget() ?: return null
        return if (target.speed != 0) SpeedThrustMode(target.speed) else null
    }

    fun getVerticalMode(): AutoFlightComputer.VerticalMode? {
        val origin: Waypoint? = getEnrouteOrigin()
        val target: Waypoint = getEnrouteTarget() ?: return null
        val textOverride =
            if (target.phaseMarker == Waypoint.PhaseMarker.LANDING) Component.translatable("mode.flightassistant.vertical.glide_slope")
            else null
        return if (origin != null) ManagedAltitudeVerticalMode(origin.posX, origin.posZ, origin.altitude, target.posX, target.posZ, target.altitude, textOverride)
        else SelectedAltitudeVerticalMode(target.altitude, textOverride)
    }

    fun getLateralMode(): AutoFlightComputer.LateralMode? {
        val origin: Waypoint? = getEnrouteOrigin()
        val target: Waypoint = getEnrouteTarget() ?: return null
        val textOverride =
            if (target.phaseMarker == Waypoint.PhaseMarker.LANDING) Component.translatable("mode.flightassistant.lateral.localizer")
            else null
        return if (origin != null) TrackNavigationLateralMode(origin.posX, origin.posZ, target.posX, target.posZ, textOverride)
        else DirectCoordinatesLateralMode(target.posX, target.posZ)
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        // TODO: config
        if (query is StatusDisplay.StatusMessageQuery && currentPhase != FlightPhase.UNKNOWN) {
            query.respond(Component.translatable("status.flightassistant.flight_plan.phase.${currentPhase.name.lowercase()}"))
        }
    }

    fun load(waypoints: List<Waypoint>) {
        this.waypoints = ArrayList(waypoints)
    }

    override fun reset() {
        currentPhase = FlightPhase.UNKNOWN
        groundSpeeds.clear()
        tracking = null
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_plan")
    }

    enum class FlightPhase {
        UNKNOWN,
        TAKEOFF,
        CLIMB,
        CRUISE,
        DESCEND,
        APPROACH,
        LANDING,
        GO_AROUND
    }

    sealed class ActiveSegment {
        data class Direct(val index: Int) : ActiveSegment()
        data class Leg(val origin: Waypoint, val targetIndex: Int) : ActiveSegment()
    }

    @Serializable
    data class Waypoint(val posX: Int = 0, val posZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, val phaseMarker: PhaseMarker = PhaseMarker.NONE, @Transient val uuid: UUID = UUID.randomUUID()) {
        fun vec3(): Vec3 {
            return Vec3(posX + 0.5, altitude.toDouble(), posZ + 0.5)
        }

        enum class PhaseMarker {
            NONE,
            TAKEOFF,
            APPROACH,
            LANDING
        }
    }
}