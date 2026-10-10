package ru.octol1ttle.flightassistant.screen.waypoints

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import java.util.*

class WaypointsScreenState(
    val waypoints: MutableList<Waypoint> = ArrayList(),
    var tracking: FlightPlanComputer.ActiveSegment? = null,
    var syncedWithComputer: Boolean = true
) {
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

    data class Waypoint(var posX: Int = 0, var posZ: Int = 0, var altitude: Int = 0, var speed: Int = 0, var phaseMarker: FlightPlanComputer.Waypoint.PhaseMarker = FlightPlanComputer.Waypoint.PhaseMarker.NONE, val uuid: UUID = UUID.randomUUID()) {
        constructor(waypoint: FlightPlanComputer.Waypoint) : this(waypoint.posX, waypoint.posZ, waypoint.altitude, waypoint.speed, waypoint.phaseMarker, waypoint.uuid)

        fun toFlightPlanWaypoint(): FlightPlanComputer.Waypoint {
            return FlightPlanComputer.Waypoint(posX, posZ, altitude, speed, phaseMarker, uuid)
        }
    }

    companion object {
        fun load(flightPlan: FlightPlanComputer): WaypointsScreenState {
            return WaypointsScreenState(flightPlan.waypoints.map { Waypoint(it) }.toMutableList(), flightPlan.tracking)
        }
    }
}