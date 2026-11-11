package ru.octol1ttle.flightassistant.api.flightplan.legacy

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Use new flight plans instead. Only kept for backwards compatibility during loading")
data class FlightPlan(val departure: DepartureData, val enroute: List<EnrouteWaypoint>, val arrival: ArrivalData) {
}
