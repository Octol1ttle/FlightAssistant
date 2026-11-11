package ru.octol1ttle.flightassistant.api.flightplan

sealed class ActiveSegment {
    data class Direct(val index: Int) : ActiveSegment()
    data class Leg(val origin: Waypoint, val targetIndex: Int) : ActiveSegment()
}
