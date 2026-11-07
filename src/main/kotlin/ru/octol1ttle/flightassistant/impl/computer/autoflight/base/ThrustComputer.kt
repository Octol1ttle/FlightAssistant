package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FAKeyMappings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerEvent
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.DeltaTimeMultiplierValidator
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.asPercentage
import ru.octol1ttle.flightassistant.api.util.extensions.filterWorking
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class ThrustComputer(computers: ComputerBus) : Computer(computers) {
    @Deprecated("Fetch every time with AvailableThrustSourceQuery")
    private val sources: MutableList<ThrustSource> = ArrayList()

    var targetThrust: Float = 0.0f
        private set
    var manualThrust: Float = 0.0f
        private set

    var lastInput: ControlInput? = null
        private set
    var noThrustSource: Boolean = false
        private set
    var reverseUnsupported: Boolean = false
        private set

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
    }

    override fun tick() {
        val thrustSource: ThrustSource? = getThrustSource()

        val inputs: List<ControlInput> = computers.dispatchQuery(TargetThrustQuery()).sortedBy { it.priority.value }
        val maxInput = inputs.getActiveHighestPriority().maxByOrNull { it.target }

        val activeInput = if (!FAKeyMappings.isHoldingThrust()) maxInput else null
        val newTarget =
            if (activeInput?.status == ControlInput.Status.ACTIVE) activeInput.target
            else manualThrust

        targetThrust = newTarget
        noThrustSource = thrustSource == null && (newTarget != 0.0f || (activeInput != null && activeInput.target != 0.0f))
        reverseUnsupported = newTarget < 0.0f && thrustSource?.supportsReverse == false

        val active: Boolean = !noThrustSource && !reverseUnsupported
        lastInput = maxInput?.copy(status = if (active) maxInput.status else ControlInput.Status.UNAVAILABLE)

        if (computers.data.automationsAllowed() && targetThrust != 0.0f) {
            thrustSource?.tickThrust(targetThrust.coerceIn((if (thrustSource.supportsReverse) -1.0f else 0.0f)..1.0f))
        }
    }

    fun getThrustSource(): ThrustSource? {
        return sources.filterWorking().filter { computers.guardedCall(it, ThrustSource::isAvailable) == true }.minByOrNull { it.priority.value }
    }

    fun setManualThrust(newThrust: Float) {
        newThrust.throwIfNotInRange(-1.0f..1.0f)

        val oldThrust = manualThrust
        val canChange = computers.dispatchQuery(AllowManualThrustChangeQuery(oldThrust, newThrust)).isEmpty()
        if (canChange) {
            manualThrust = newThrust
            computers.dispatchEvent(ManualThrustChangeEvent(oldThrust, newThrust))
        }
    }

    fun tickManualThrust(sign: Float) {
        if (sign != 0.0f) {
            val newThrust = manualThrust + FATickCounter.timePassed / 3 * sign
            setManualThrust(newThrust.coerceIn(-1.0f..1.0f))
        }
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is StatusDisplay.StatusMessageQuery) {
            // TODO: show actual thrust output and requested thrust (both by user and autoflight)
            if (getThrustSource() != null || targetThrust != 0.0f) {
                query.respond(Component.translatable("status.flightassistant.thrust", targetThrust.asPercentage()))
            }
        }
    }

    private class ThrustValidator : ComputerQuery.Validator<ControlInput> {
        override fun validate(response: ControlInput) {
            response.target.throwIfNotInRange(-1.0f..1.0f)
        }
    }

    class TargetThrustQuery : ComputerQuery<ControlInput>(ThrustValidator(), DeltaTimeMultiplierValidator())
    data class AllowManualThrustChangeQuery(val oldThrust: Float, val newThrust: Float) : ComputerQuery<Unit>()
    data class ManualThrustChangeEvent(val oldThrust: Float, val newThrust: Float) : ComputerEvent

    override fun reset() {
        targetThrust = 0.0f
        lastInput = null
        noThrustSource = false
        reverseUnsupported = false
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("thrust")
    }
}
