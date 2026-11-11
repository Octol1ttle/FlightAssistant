package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.RollSource
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.*
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import kotlin.math.abs

class RollComputer(computers: ComputerBus) : Computer(computers) {
    override fun renderTick() {
        val inputs: List<ControlInput> = computers.dispatchQuery(TargetRollQuery).sortedBy { it.priority.value }
        val finalInput: ControlInput = inputs.getActiveHighestPriority().firstOrNull() ?: return

        if (computers.data.automationsAllowed() && finalInput.status == ControlInput.Status.ACTIVE) {
            val sources = computers.dispatchQuery(ActiveRollSourceQuery)
            val rollSource: RollSource = sources.firstOrNull() ?: return
            smoothSetRoll(rollSource, finalInput.target, finalInput.deltaTimeMultiplier)
        }
    }

    private fun smoothSetRoll(rollSource: RollSource, target: Float, deltaTimeMultiplier: Float) {
        val diff: Float = findShortestPath(rollSource.getRoll(), target, 360.0f)

        val closeDistanceMultiplier: Float =
            if (diff == 0.0f) 1.0f
            else (1.0f / abs(diff)).coerceAtLeast(1.0f)

        val delta: Float = diff * (FATickCounter.timePassed * deltaTimeMultiplier * closeDistanceMultiplier).coerceIn(0.0f..1.0f)
        rollSource.addRoll(delta)
    }

    override fun reset() {
    }

    private class RollValidator : ComputerQuery.Validator<ControlInput> {
        override fun validate(response: ControlInput) {
            response.target.throwIfNotInRange(-180.0f..180.0f)
        }
    }

    object ActiveRollSourceQuery : ComputerQuery<RollSource>()
    object TargetRollQuery : ComputerQuery<ControlInput>(RollValidator(), DeltaTimeMultiplierValidator())

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("roll")
    }
}
