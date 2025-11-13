package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import kotlin.math.abs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.EntityTurnEvents
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange

class PitchComputer(computers: ComputerBus) : Computer(computers), FlightController {
    private val controllers: MutableList<FlightController> = ArrayList()

    var minimumPitch: ControlInput? = null
        private set
    var maximumPitch: ControlInput? = null
        private set
    var activeInput: ControlInput? = null
        private set

    override fun subscribeToEvents() {
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
        EntityTurnEvents.X_ROT.register { mcPitchDelta, output ->
            if (canMoveOrBlockPitch()) {
                val pitchDelta: Float = -mcPitchDelta

                val oldPitch: Float? = computers.guardedCall(computers.data) { it.pitch }
                if (oldPitch == null) {
                    computers.protections.protectionsLost = true
                    return@register
                }
                val newPitch: Float = oldPitch + pitchDelta

                val min: ControlInput? = this.minimumPitch
                val max: ControlInput? = this.maximumPitch
                if (max != null && max.status == ControlInput.Status.ACTIVE && pitchDelta > 0.0f && newPitch > max.target) {
                    output.add(ControlInput(-(max.target - oldPitch).coerceAtLeast(0.0f), priority = max.priority))
                } else if (min != null && min.status == ControlInput.Status.ACTIVE && pitchDelta < 0.0f && newPitch < min.target) {
                    output.add(ControlInput(-(min.target - oldPitch).coerceAtMost(0.0f), priority = min.priority))
                }
            }
        }
    }

    override fun invokeEvents() {
        PitchControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        updateSafePitches()

        val inputs: List<ControlInput> = controllers.filterWorking().mapNotNull { computers.guardedCall(it, FlightController::getPitchInput) }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            activeInput = null
            return
        }

        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }
        if (finalInput == null) {
            activeInput = null
            return
        }

        activeInput = finalInput
    }

    override fun renderTick() {
        val input = activeInput ?: return

        if (canMoveOrBlockPitch() && input.status == ControlInput.Status.ACTIVE) {
            var target: Float = input.target
            if (!input.priority.isHigherOrSame(minimumPitch?.priority)) {
                target = target.coerceAtLeast(minimumPitch!!.target + 1.0f)
            }
            if (!input.priority.isHigherOrSame(maximumPitch?.priority)) {
                target = target.coerceAtMost(maximumPitch!!.target - 1.0f)
            }
            smoothSetPitch(computers.data.player, computers.data.pitch, target.throwIfNotInRange(-90.0f..90.0f), input.deltaTimeMultiplier.throwIfNotInRange(0.001f..Float.MAX_VALUE))
        }
    }

    private fun canMoveOrBlockPitch(): Boolean {
        return computers.data.automationsAllowed() && !computers.protections.protectionsLost
    }

    private fun updateSafePitches() {
        val maximums: List<ControlInput> = computers.dispatchQuery(MaximumPitchQuery()).sortedBy { it.priority.value }
        maximumPitch = maximums.getActiveHighestPriority().minByOrNull { it.target }
        val max: ControlInput? = maximumPitch

        val minimums: List<ControlInput> = computers.dispatchQuery(MinimumPitchQuery()).sortedBy { it.priority.value }
        minimumPitch = minimums.getActiveHighestPriority().maxByOrNull { it.target }
        val min: ControlInput? = minimumPitch

        if (max != null && min != null && max.priority.isHigherOrSame(min.priority)) {
            minimumPitch = min.copy(target = min.target.coerceAtMost(max.target))
        }
    }

    override fun getPitchInput(): ControlInput? {
        if (!computers.data.flying) {
            return null
        }

        val max: ControlInput? = maximumPitch
        if (max != null && computers.data.pitch > max.target) {
            return max
        }

        val min: ControlInput? = minimumPitch
        if (min != null && computers.data.pitch < min.target) {
            return min
        }

        return null
    }

    private fun smoothSetPitch(player: Player, current: Float, target: Float, deltaTimeMultiplier: Float) {
        val diff: Float = target - current

        val closeDistanceMultiplier: Float =
            if (diff == 0.0f) 1.0f
            else (1.0f / abs(diff)).coerceAtLeast(1.0f)

        val delta: Float = diff * (FATickCounter.timePassed * deltaTimeMultiplier * closeDistanceMultiplier).coerceIn(0.0f..1.0f)
        player.xRot -= delta
        player.xRotO -= delta
    }

    override fun reset() {
        minimumPitch = null
        maximumPitch = null
        activeInput = null
    }

    class MinimumPitchQuery : ComputerQuery<ControlInput>() {
        override fun validateResponse(response: ControlInput) {
            response.target.throwIfNotInRange(-90.0f..90.0f)
            response.deltaTimeMultiplier.throwIfNotInRange(0.001f..Float.MAX_VALUE)
        }
    }
    class MaximumPitchQuery : ComputerQuery<ControlInput>() {
        override fun validateResponse(response: ControlInput) {
            response.target.throwIfNotInRange(-90.0f..90.0f)
            response.deltaTimeMultiplier.throwIfNotInRange(0.001f..Float.MAX_VALUE)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("pitch")
    }
}
