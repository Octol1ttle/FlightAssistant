package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.SpeedReferenceVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.TakeoffThrustMode
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class FlightPlanComputer(computers: ComputerBus) : Computer(computers) {
    var currentPhase: FlightPhase = FlightPhase.ON_GROUND
        private set
    var departureData: DepartureData = DepartureData.DEFAULT
    val enrouteData: MutableList<EnrouteWaypoint> = ArrayList()

    override fun tick() {
        currentPhase = updateFlightPhase()
    }

    private fun updateFlightPhase(): FlightPhase {
        if (!departureData.isDefault() && abs(computers.data.position.x - departureData.coordinatesX) < 20 && abs(computers.data.position.z - departureData.coordinatesZ) < 20) {
            return FlightPhase.TAKEOFF
        }

        if (!computers.data.flying) {
            return FlightPhase.ON_GROUND
        }
        if (currentPhase == FlightPhase.TAKEOFF) {
            return FlightPhase.TAKEOFF
        }

        return FlightPhase.UNKNOWN
    }

    fun getThrustMode(): AutoFlightComputer.ThrustMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> TakeoffThrustMode(departureData)
            else -> null
        }
    }

    fun getVerticalMode(): AutoFlightComputer.VerticalMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> SpeedReferenceVerticalMode(departureData.minimumClimbSpeed)
            else -> null
        }
    }

    fun getLateralMode(): AutoFlightComputer.LateralMode? {
        return when (currentPhase) {
            else -> null
        }
    }

    override fun <Response> processQuery(query: ComputerQuery<Response>) {
        if (query is StatusDisplay.StatusMessageQuery && currentPhase != FlightPhase.UNKNOWN && (!departureData.isDefault() || enrouteData.isNotEmpty())) {
            query.respond(Component.translatable("status.flightassistant.flight_phase.${currentPhase.name.lowercase()}"))
        }
    }

    override fun reset() {
        currentPhase = FlightPhase.UNKNOWN
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_plan")
    }

    enum class FlightPhase {
        UNKNOWN,
        ON_GROUND,
        TAKEOFF,
        CLIMB
    }

    data class DepartureData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val takeoffThrust: Float = 1.0f, val minimumClimbSpeed: Int = 15) {
        fun isDefault(): Boolean {
            return this == DEFAULT
        }

        companion object {
            val DEFAULT: DepartureData = DepartureData()
        }
    }
    data class EnrouteWaypoint(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, val active: Active? = null) {
        enum class Active {
            ORIGIN,
            TARGET
        }
    }
}
