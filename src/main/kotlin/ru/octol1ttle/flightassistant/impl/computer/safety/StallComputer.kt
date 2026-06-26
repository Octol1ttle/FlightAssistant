package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.ThrustComputer

class StallComputer(computers: ComputerBus) : Computer(computers) {
    var status: Status = Status.SAFE
        private set
    private var maximumSafePitch: Float = 90.0f

    override fun tick() {
        val angleOfAttack: Float = computers.data.pitch - computers.data.flightPitch
        status =
            if (computers.data.flying && !computers.data.fallDistanceSafe && angleOfAttack > 90.0f)
                if (status == Status.FULL_STALL || computers.data.velocityPerSecond.y <= -10) Status.FULL_STALL else Status.APPROACHING_STALL
            else Status.SAFE

        maximumSafePitch = (computers.data.flightPitch + 90.0).coerceAtMost(computers.data.forwardVelocityPerSecond.length() * 3.0 + 45.0).toFloat()
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is ThrustComputer.TargetThrustQuery && status != Status.SAFE) {
            query.respond(ControlInput(
                1.0f,
                Component.translatable("mode.flightassistant.thrust.toga"),
                ControlInput.Priority.HIGHEST,
                status = ControlInput.Status.fromBooleans(status == Status.FULL_STALL, enabled = FAConfig.safety.stallAutoThrust)
            ))
        }

        if (query is PitchComputer.MaximumPitchQuery && !computers.data.fallDistanceSafe) {
            query.respond(ControlInput(
                (maximumSafePitch - 5.0f).coerceAtMost(90.0f),
                Component.translatable("mode.flightassistant.vertical.stall_protection"),
                ControlInput.Priority.HIGHEST,
                1.5f,
                ControlInput.Status.fromBooleans(true, enabled = FAConfig.safety.stallLimitPitch)
            ))
        }
    }

    override fun reset() {
        status = Status.SAFE
        maximumSafePitch = 90.0f
    }

    enum class Status {
        FULL_STALL,
        APPROACHING_STALL,
        SAFE
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("stall")
    }
}
