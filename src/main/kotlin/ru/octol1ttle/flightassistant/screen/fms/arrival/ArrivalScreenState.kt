package ru.octol1ttle.flightassistant.screen.fms.arrival

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

data class ArrivalScreenState(
    var coordinatesX: Int = 0,
    var coordinatesZ: Int = 0,
    var elevation: Int = 0,
    var landingThrustPercent: Int = 0,
    var minimums: Int = 0,
    var minimumsType: FlightPlanComputer.ArrivalData.MinimumsType = FlightPlanComputer.ArrivalData.MinimumsType.ABSOLUTE,
    var goAroundAltitude: Int = 0,
    var approachReEntryWaypointIndex: Int = 0
) {
    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.arrivalData = FlightPlanComputer.ArrivalData(coordinatesX, coordinatesZ, elevation, landingThrustPercent / 100.0f, minimums, minimumsType, goAroundAltitude, approachReEntryWaypointIndex)
    }

    companion object {
        fun load(data: FlightPlanComputer.ArrivalData): ArrivalScreenState {
            return ArrivalScreenState(data.coordinatesX, data.coordinatesZ, data.elevation, (data.landingThrust / 100.0f).toInt(), data.minimums, data.minimumsType, data.goAroundAltitude, data.approachReEntryWaypointIndex)
        }
    }
}