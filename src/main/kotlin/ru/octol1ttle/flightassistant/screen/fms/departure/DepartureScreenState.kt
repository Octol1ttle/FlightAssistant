package ru.octol1ttle.flightassistant.screen.fms.departure

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

data class DepartureScreenState(
    var coordinatesX: Int = 0,
    var coordinatesZ: Int = 0,
    var elevation: Int = 0,
    var takeoffThrustPercent: Int = 100,
    var minimumClimbSpeed: Int = 15
) {
    fun load(flightPlan: FlightPlanComputer) {
        TODO()
    }

    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.departureData = FlightPlanComputer.DepartureData(coordinatesX, coordinatesZ, elevation, takeoffThrustPercent / 100.0f, minimumClimbSpeed)
    }
}