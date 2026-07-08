package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import kotlin.math.abs
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.DeltaTimeMultiplierValidator
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange

class HeadingComputer(computers: ComputerBus) : Computer(computers) {
    var activeInput: ControlInput? = null
        private set

    override fun tick() {
        val inputs: List<ControlInput> = computers.dispatchQuery(TargetHeadingQuery()).sortedBy { it.priority.value }
        activeInput = inputs.getActiveHighestPriority().firstOrNull()
    }

    override fun renderTick() {
        val input = activeInput ?: return

        if (computers.data.automationsAllowed() && input.status == ControlInput.Status.ACTIVE) {
            smoothSetHeading(computers.data.player, computers.data.heading, input.target.throwIfNotInRange(0.0f..360.0f), input.deltaTimeMultiplier.throwIfNotInRange(0.001f..Float.MAX_VALUE))
        }
    }

    private fun smoothSetHeading(player: Player, current: Float, target: Float, deltaTimeMultiplier: Float) {
        val diff: Float = findShortestPath(current, target, 360.0f)

        val closeDistanceMultiplier: Float =
            if (diff == 0.0f) 1.0f
            else (1.0f / abs(diff)).coerceAtLeast(1.0f)

        val delta: Float = diff * (FATickCounter.timePassed * deltaTimeMultiplier * closeDistanceMultiplier).coerceIn(0.0f..1.0f)
        player.yRot += delta
        player.yRotO += delta
    }

    override fun reset() {
        activeInput = null
    }

    private class HeadingValidator : ComputerQuery.Validator<ControlInput> {
        override fun validate(response: ControlInput) {
            response.target.throwIfNotInRange(0.0f..360.0f)
        }
    }

    class TargetHeadingQuery : ComputerQuery<ControlInput>(HeadingValidator(), DeltaTimeMultiplierValidator())

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("heading")
    }
}
