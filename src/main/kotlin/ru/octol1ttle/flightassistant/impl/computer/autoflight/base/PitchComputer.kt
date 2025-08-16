package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import kotlin.math.abs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.EntityTurnEvents
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.requireIn

class PitchComputer(computers: ComputerBus) : Computer(computers), FlightController {
    private val limiters: MutableList<PitchLimiter> = ArrayList()
    private val controllers: MutableList<FlightController> = ArrayList()
    internal var manualOverride: Boolean = false

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
                if (max != null && max.active && pitchDelta > 0.0f && newPitch > max.target) {
                    output.add(ControlInput(-(max.target - oldPitch).coerceAtLeast(0.0f), max.priority))
                } else if (min != null && min.active && pitchDelta < 0.0f && newPitch < min.target) {
                    output.add(ControlInput(-(min.target - oldPitch).coerceAtMost(0.0f), min.priority))
                }
            }
        }
    }

    override fun invokeEvents() {
        PitchLimiterRegistrationCallback.EVENT.invoker().register(limiters::add)
        PitchControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        updateSafePitches()

        val inputs: List<ControlInput> = controllers.filterWorking().mapNotNull { computers.guardedCall(it, FlightController::getPitchInput) }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            activeInput = null
            return
        }

        val pitch: Float = computers.data.pitch
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }
        if (finalInput == null) {
            activeInput = null
            return
        }

        activeInput = finalInput
        if (canMoveOrBlockPitch() && finalInput.active) {
            var target: Float = finalInput.target
            if (!finalInput.priority.isHigherOrSame(minimumPitch?.priority)) {
                target = target.coerceAtLeast(minimumPitch!!.target)
            }
            if (!finalInput.priority.isHigherOrSame(maximumPitch?.priority)) {
                target = target.coerceAtMost(maximumPitch!!.target)
            }
            smoothSetPitch(computers.data.player, pitch, target.requireIn(-90.0f..90.0f), finalInput.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE))
        }
    }

    private fun canMoveOrBlockPitch(): Boolean {
        return !manualOverride && !computers.protections.protectionsLost && computers.data.automationsAllowed()
    }

    private fun updateSafePitches() {
        val maximums: List<ControlInput> = limiters.filterWorking().mapNotNull { it.getMaximumPitch() }.sortedBy { it.priority.value }
        maximumPitch = maximums.getActiveHighestPriority().minByOrNull { it.target }
        val max: ControlInput? = maximumPitch
        if (max != null) {
            max.target.requireIn(-90.0f..90.0f)
            max.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE)
        }

        val minimums: List<ControlInput> = limiters.filterWorking().mapNotNull { it.getMinimumPitch() }.sortedBy { it.priority.value }
        minimumPitch = minimums.getActiveHighestPriority().maxByOrNull { it.target }
        val min: ControlInput? = minimumPitch
        if (min != null) {
            min.target.requireIn(-90.0f..90.0f)
            min.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE)
        }

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
            if (computers.data.pitch - max.target < 5.0f) {
                return max.copy(text = null)
            }
            return max
        }

        val min: ControlInput? = minimumPitch
        if (min != null && computers.data.pitch < min.target) {
            if (min.target - computers.data.pitch < 5.0f) {
                return min.copy(text = null)
            }
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
        manualOverride = true
        minimumPitch = null
        maximumPitch = null
        activeInput = null
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("pitch")
    }
}
