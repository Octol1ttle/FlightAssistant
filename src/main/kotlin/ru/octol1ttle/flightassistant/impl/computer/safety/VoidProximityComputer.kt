package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.extensions.bottomY
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer

class VoidProximityComputer(computers: ComputerBus) : Computer(computers), FlightController {
    var status: Status = Status.ABOVE_GROUND
        private set

    override fun subscribeToEvents() {
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick() {
        status = if (computers.gpws.groundY != null) {
            Status.ABOVE_GROUND
        } else {
            val heightAboveDamageAltitude: Double = computers.data.altitude - computers.data.voidY
            if (heightAboveDamageAltitude > 16.0) {
                Status.CLEAR_OF_DAMAGE_ALTITUDE
            } else if (status != Status.REACHED_DAMAGE_ALTITUDE && heightAboveDamageAltitude > 1.0) {
                Status.APPROACHING_DAMAGE_ALTITUDE
            } else {
                Status.REACHED_DAMAGE_ALTITUDE
            }
        }
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is PitchComputer.MinimumPitchQuery && status != Status.ABOVE_GROUND) {
            query.respond(ControlInput(
                (-90.0f + (computers.data.level.bottomY - (computers.data.altitude + computers.data.velocityPerSecond.y)) / 64.0f * 105.0f).toFloat()
                    .coerceIn(-35.0f..15.0f),
                Component.translatable("mode.flightassistant.vertical.void_protection"),
                ControlInput.Priority.HIGH,
                status = ControlInput.Status.fromBooleans(true, enabled = FAConfig.safety.voidLimitPitch)
            ))
        }
    }

    override fun getPitchInput(): ControlInput? {
        if (status <= Status.APPROACHING_DAMAGE_ALTITUDE) {
            return ControlInput(90.0f, Component.translatable("mode.flightassistant.vertical.void_escape"), ControlInput.Priority.HIGH,
                status = ControlInput.Status.fromBooleans(status == Status.REACHED_DAMAGE_ALTITUDE && computers.thrust.current == 1.0f && !computers.thrust.noThrustSource,
                    enabled = FAConfig.safety.voidAutoPitch)
            )
        }

        return null
    }

    override fun getThrustInput(): ControlInput? {
        if (status <= Status.APPROACHING_DAMAGE_ALTITUDE) {
            return ControlInput(
                1.0f,
                Component.translatable("mode.flightassistant.thrust.toga"),
                ControlInput.Priority.HIGH,
                status = ControlInput.Status.fromBooleans(status == Status.REACHED_DAMAGE_ALTITUDE, enabled = FAConfig.safety.voidAutoThrust)
            )
        }

        return null
    }

    override fun reset() {
        status = Status.ABOVE_GROUND
    }

    enum class Status {
        REACHED_DAMAGE_ALTITUDE,
        APPROACHING_DAMAGE_ALTITUDE,
        CLEAR_OF_DAMAGE_ALTITUDE,
        ABOVE_GROUND
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("void_proximity")
    }
}
