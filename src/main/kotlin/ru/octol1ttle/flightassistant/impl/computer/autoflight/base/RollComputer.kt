package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import kotlin.math.abs
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSource
import ru.octol1ttle.flightassistant.api.autoflight.roll.RollSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.DeltaTimeMultiplierValidator
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange

class RollComputer(computers: ComputerBus) : Computer(computers) {
    private val sources: MutableList<RollSource> = ArrayList()

    override fun invokeEvents() {
        RollSourceRegistrationCallback.EVENT.invoker().register(sources::add)
    }

    override fun renderTick() {
        val inputs: List<ControlInput> = computers.dispatchQuery(TargetRollQuery()).sortedBy { it.priority.value }
        val finalInput: ControlInput = inputs.getActiveHighestPriority().firstOrNull() ?: return

        if (computers.data.automationsAllowed() && finalInput.status == ControlInput.Status.ACTIVE) {
            val rollSource: RollSource = sources.filterWorking().singleOrNull { computers.guardedCall(it, RollSource::isActive) == true } ?: return
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

    class TargetRollQuery : ComputerQuery<ControlInput>(RollValidator(), DeltaTimeMultiplierValidator())

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("roll")
    }
}
