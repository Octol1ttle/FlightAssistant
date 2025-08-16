package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FAKeyMappings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.requireIn
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class ThrustComputer(computers: ComputerBus) : Computer(computers) {
    private val sources: MutableList<ThrustSource> = ArrayList()
    private val controllers: MutableList<FlightController> = ArrayList()

    private var lastChangeAutomatic: Boolean = false

    var current: Float = 0.0f
        private set

    var activeInput: ControlInput? = null
        private set
    var noThrustSource: Boolean = false
        private set
    var reverseUnsupported: Boolean = false
        private set
    var thrustLocked: Boolean = false // TODO: what an annoying mechanic
        private set

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        val thrustSource: ThrustSource? = getThrustSource()

        val inputs: List<ControlInput> = controllers.filterWorking().mapNotNull { computers.guardedCall(it, FlightController::getThrustInput) }.sortedBy { it.priority.value }
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }

        noThrustSource = false
        reverseUnsupported = false

        if (finalInput?.active == true && !FAKeyMappings.isHoldingThrust()) {
            setTarget(finalInput.target, finalInput)
            activeInput = finalInput
            thrustLocked = false
        } else if (current == 0.0f) {
            noThrustSource = thrustSource == null && finalInput != null && finalInput.target != 0.0f
            activeInput = finalInput
            thrustLocked = false

            return
        } else {
            activeInput = null
            thrustLocked = lastChangeAutomatic
            reverseUnsupported = current < 0.0f && thrustSource?.supportsReverse == false
        }

        noThrustSource = thrustSource == null && activeInput?.target != 0.0f
        current.requireIn(-1.0f..1.0f)

        val active: Boolean = !noThrustSource && !reverseUnsupported
        activeInput = activeInput?.copy(active = active)

        if (computers.data.automationsAllowed()) {
            thrustSource?.tickThrust(current.coerceIn((if (thrustSource.supportsReverse) -1.0f else 0.0f)..1.0f))
        }
    }

    fun getThrustSource(): ThrustSource? {
        return sources.filterWorking().filter { computers.guardedCall(it, ThrustSource::isAvailable) == true }.minByOrNull { it.priority.value }
    }

    fun setTarget(target: Float, input: ControlInput? = null) {
        val oldThrust: Float = current
        if (oldThrust != target || input == null) {
            current = target.requireIn(-1.0f..1.0f)
            ThrustChangeCallback.EVENT.invoker().onThrustChange(oldThrust, current, input)
            lastChangeAutomatic = input != null
        }
    }

    fun tickTarget(sign: Float) {
        if (sign != 0.0f) {
            setTarget((current + FATickCounter.timePassed / 3 * sign).coerceIn(-1.0f..1.0f), null)
        }
    }

    fun getOptimumClimbPitch(): Float {
        val thrustSource: ThrustSource? = getThrustSource()
        if (thrustSource != null) {
            return thrustSource.optimumClimbPitch
        }

        return 55.0f
    }

    fun getAltitudeHoldPitch(): Float {
        val thrustSource: ThrustSource? = getThrustSource()
        if (thrustSource != null) {
            return thrustSource.altitudeHoldPitch
        }

        return 5.0f
    }

    override fun <Response> processQuery(query: ComputerQuery<Response>) {
        if (query is StatusDisplay.StatusMessageQuery) {
            // TODO: show actual thrust output and requested thrust (both by user and autoflight)
            if (getThrustSource() != null || current != 0.0f) {
                query.respond(Component.translatable("status.flightassistant.thrust", "%.1f".format(current * 100) + "%"))
            }
        }
    }

    override fun reset() {
        lastChangeAutomatic = false
        current = 0.0f
        activeInput = null
        noThrustSource = false
        reverseUnsupported = false
        thrustLocked = false
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("thrust")
    }
}
