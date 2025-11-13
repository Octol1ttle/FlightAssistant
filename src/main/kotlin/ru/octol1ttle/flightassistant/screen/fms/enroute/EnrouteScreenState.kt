package ru.octol1ttle.flightassistant.screen.fms.enroute

import java.util.UUID
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class EnrouteScreenState(
    val waypoints: MutableList<Waypoint> = ArrayList()
) {
    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.enrouteData.clear()
        flightPlan.enrouteData.addAll(this.waypoints.map(Waypoint::toEnrouteWaypoint))
    }

    fun equals(other: EnrouteScreenState): Boolean {
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

    data class Waypoint(var coordinatesX: Int = 0, var coordinatesZ: Int = 0, var altitude: Int = 0, var speed: Int = 0, var active: FlightPlanComputer.EnrouteWaypoint.Active? = null, val uuid: UUID = UUID.randomUUID()) {
        var flightPlanWaypoint: FlightPlanComputer.EnrouteWaypoint? = null

        constructor(waypoint: FlightPlanComputer.EnrouteWaypoint) : this(waypoint.coordinatesX, waypoint.coordinatesZ, waypoint.altitude, waypoint.speed, waypoint.active, waypoint.uuid) {
            this.flightPlanWaypoint = waypoint
        }

        fun toEnrouteWaypoint(): FlightPlanComputer.EnrouteWaypoint {
            val enrouteWaypoint = FlightPlanComputer.EnrouteWaypoint(coordinatesX, coordinatesZ, altitude, speed, active, uuid)
            this.flightPlanWaypoint = enrouteWaypoint
            return enrouteWaypoint
        }
    }

    companion object {
        fun load(waypoints: List<FlightPlanComputer.EnrouteWaypoint>): EnrouteScreenState {
            return EnrouteScreenState(waypoints.map { Waypoint(it) }.toMutableList())
        }
    }
}