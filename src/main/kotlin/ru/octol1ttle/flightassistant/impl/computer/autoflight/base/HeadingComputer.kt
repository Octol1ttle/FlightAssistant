package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import kotlin.math.abs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.heading.HeadingControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.api.util.requireIn

class HeadingComputer(computers: ComputerBus) : Computer(computers) {
    private val controllers: MutableList<FlightController> = ArrayList()
    var activeInput: ControlInput? = null
        private set

    override fun invokeEvents() {
        HeadingControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        val inputs: List<ControlInput> = controllers.filterWorking().mapNotNull { computers.guardedCall(it, FlightController::getHeadingInput) }.sortedBy { it.priority.value }
        if (inputs.isEmpty()) {
            activeInput = null
            return
        }

        val heading: Float = computers.data.heading
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().firstOrNull()
        if (finalInput == null) {
            activeInput = null
            return
        }

        activeInput = finalInput
        if (computers.data.automationsAllowed() && finalInput.active) {
            smoothSetHeading(computers.data.player, heading, finalInput.target.requireIn(0.0f..360.0f), finalInput.deltaTimeMultiplier.requireIn(0.001f..Float.MAX_VALUE))
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

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("heading")
    }
}
