package ru.octol1ttle.flightassistant.screen.waypoints

import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.flightplan.ActiveSegment
import ru.octol1ttle.flightassistant.api.flightplan.Waypoint.PhaseMarker
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import java.util.*

class WaypointsScreenState(
    val waypoints: MutableList<Waypoint> = ArrayList(),
    var tracking: ActiveSegment? = null,
    var syncedWithComputer: Boolean = true
) {
    val problems: MutableList<Problem> = ArrayList()

    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.waypoints.clear()
        flightPlan.waypoints.addAll(this.waypoints.map(Waypoint::toFlightPlanWaypoint))
        flightPlan.tracking = tracking
    }

    fun equals(other: WaypointsScreenState): Boolean {
        if (this.tracking != other.tracking) {
            return false
        }
        if (this.waypoints.size != other.waypoints.size) {
            return false
        }
        this.waypoints.forEachIndexed { i, waypoint ->
            if (other.waypoints[i] != waypoint) {
                return false
            }
        }
        return true
    }

    fun refreshProblems() {
        problems.clear()

        if (waypoints.size == 1) {
            problems.add(Problem(null, true, Component.translatable("menu.flightassistant.fms.problems.not_enough_waypoints")))
        }

        waypoints.zipWithNext().forEach { (waypoint, next) ->
            if (waypoint.posX == next.posX && waypoint.posZ == next.posZ) {
                val text = Component.translatable(
                    "menu.flightassistant.fms.problems.adjacent_with_same_position",
                    waypoint.posX,
                    waypoint.posZ
                )
                problems.add(Problem(waypoint, true, text))
                problems.add(Problem(next, true, text))
            }
        }

        val landing = waypoints.singleOrNull { it.phaseMarker == PhaseMarker.LANDING }
        if (landing != null && !waypoints.any { it.phaseMarker == PhaseMarker.APPROACH}) {
            problems.add(Problem(landing, true, Component.translatable("menu.flightassistant.fms.problems.no_approach_start")))
        }
    }

    data class Waypoint(var posX: Int = 0, var posZ: Int = 0, var altitude: Int = 0, var speed: Int = 0, var phaseMarker: PhaseMarker = PhaseMarker.NONE, val uuid: UUID = UUID.randomUUID()) {
        constructor(waypoint: ru.octol1ttle.flightassistant.api.flightplan.Waypoint) : this(waypoint.posX, waypoint.posZ, waypoint.altitude, waypoint.speed, waypoint.phaseMarker, waypoint.uuid)

        fun toFlightPlanWaypoint(): ru.octol1ttle.flightassistant.api.flightplan.Waypoint {
            return ru.octol1ttle.flightassistant.api.flightplan.Waypoint(posX, posZ, altitude, speed, phaseMarker, uuid)
        }
    }

    data class Problem(val waypoint: Waypoint?, val severe: Boolean, val text: Component)

    companion object {
        fun load(flightPlan: FlightPlanComputer): WaypointsScreenState {
            return WaypointsScreenState(flightPlan.waypoints.map { Waypoint(it) }.toMutableList(), flightPlan.tracking)
        }
    }
}
