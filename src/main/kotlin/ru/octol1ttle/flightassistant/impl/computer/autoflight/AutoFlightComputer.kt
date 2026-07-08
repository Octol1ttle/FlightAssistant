package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FAKeyMappings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.event.EntityTurnEvents
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.HeadingComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.ThrustComputer

class AutoFlightComputer(computers: ComputerBus) : Computer(computers) {
    var flightDirectors: Boolean = false
        private set

    var autoThrust: Boolean = false
        private set
    var autoThrustAlert: Boolean = false

    var autopilot: Boolean = false
        private set
    var autopilotAlert: Boolean = false

    private var pitchResistance: Float = 0.0f
    private var headingResistance: Float = 0.0f

    var selectedThrustMode: ThrustMode? = null
    var selectedVerticalMode: VerticalMode? = null
    var selectedLateralMode: LateralMode? = null

    val activeThrustMode: ThrustMode?
        get() = selectedThrustMode ?: computers.plan.getThrustMode()

    val activeVerticalMode: VerticalMode?
        get() = selectedVerticalMode ?: computers.plan.getVerticalMode()

    val activeLateralMode: LateralMode?
        get() = selectedLateralMode ?: computers.plan.getLateralMode()

    override fun subscribeToEvents() {
        ThrustChangeCallback.EVENT.register(ThrustChangeCallback { _, _, input ->
            if (input == null) {
                setAutoThrust(false, alert = false)
            }
        })
        EntityTurnEvents.X_ROT.register(EntityTurnEvents.EntityTurn { pitchDelta, output ->
            if (computers.data.flying && autopilot) {
                pitchResistance += abs(pitchDelta)
                if (pitchResistance < 20.0f) {
                    output.add(ControlInput(0.0f, priority = ControlInput.Priority.NORMAL))
                    return@EntityTurn
                }
                setAutoPilot(false, alert = true)
            }

            pitchResistance = 0.0f
        })
        EntityTurnEvents.Y_ROT.register(EntityTurnEvents.EntityTurn { headingDelta, output ->
            if (computers.data.flying && autopilot) {
                headingResistance += abs(headingDelta)
                if (headingResistance < 40.0f) {
                    output.add(ControlInput(0.0f, priority = ControlInput.Priority.NORMAL))
                    return@EntityTurn
                }
                setAutoPilot(false, alert = true)
            }

            headingResistance = 0.0f
        })
    }

    override fun tick() {
        if (computers.protections.protectionsLost || !computers.chunk.isCurrentLoaded) {
            if (this.autoThrust) {
                setAutoThrust(false, alert = true)
            }
            if (this.autopilot) {
                setAutoPilot(false, alert = true)
            }
            return
        }

        if (FAKeyMappings.globalAutomationOverride.isDown) {
            setAutoThrust(false, alert = false)
            setAutoPilot(false, alert = false)
        }

        pitchResistance = (pitchResistance - FATickCounter.timePassed * 10.0f).coerceAtLeast(0.0f)
        headingResistance = (headingResistance - FATickCounter.timePassed * 20.0f).coerceAtLeast(0.0f)
    }

    fun setFlightDirectors(flightDirectors: Boolean) {
        this.flightDirectors = flightDirectors
    }

    fun setAutoThrust(autoThrust: Boolean, alert: Boolean? = null) {
        if (alert != null) {
            this.autoThrustAlert = this.autoThrust && !autoThrust && alert
        }
        this.autoThrust = autoThrust
    }

    fun setAutoPilot(autopilot: Boolean, alert: Boolean? = null) {
        if (alert != null) {
            this.autopilotAlert = this.autopilot && !autopilot && alert
        }
        this.autopilot = autopilot
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is ThrustComputer.TargetThrustQuery) {
            if (!autoThrust) {
                return
            }

            val mode = activeThrustMode ?: return
            val input = mode.getControlInput(computers) ?: return
            query.respond(input.copy(text = mode.textOverride ?: input.text))
        }

        if (!flightDirectors && !autopilot) {
            return
        }

        if (query is PitchComputer.TargetPitchQuery) {
            val mode = activeVerticalMode ?: return
            val input = mode.getControlInput(computers) ?: return
            query.respond(input.copy(text = mode.textOverride ?: input.text, deltaTimeMultiplier = 1.5f, status = ControlInput.Status.fromBooleans(autopilot)))
        }

        if (query is HeadingComputer.TargetHeadingQuery) {
            val mode = activeLateralMode ?: return
            val input = mode.getControlInput(computers) ?: return
            query.respond(input.copy(text = mode.textOverride ?: input.text, deltaTimeMultiplier = 1.5f, status = ControlInput.Status.fromBooleans(autopilot)))
        }

        if (query is RollComputer.TargetRollQuery && autopilot) {
            query.respond(ControlInput(0.0f, deltaTimeMultiplier = 2.0f))
        }
    }

    override fun reset() {
        flightDirectors = false
        if (autoThrust) {
            autoThrustAlert = true
        }
        autoThrust = false
        if (autopilot) {
            autopilotAlert = true
        }
        autopilot = false
        pitchResistance = 0.0f
        headingResistance = 0.0f
    }

    interface AutoFlightMode {
        val textOverride: Component?

        fun getControlInput(computers: ComputerBus): ControlInput?
    }

    interface FollowsSpeedMode : AutoFlightMode {
        val targetSpeed: Int
    }

    interface FollowsPitchMode : AutoFlightMode {
        val targetPitch: Float
    }

    interface FollowsAltitudeMode : AutoFlightMode {
        val targetAltitude: Int
    }

    interface FollowsHeadingMode : AutoFlightMode {
        val targetHeading: Int
    }

    interface FollowsCoordinatesMode : AutoFlightMode {
        val targetX: Int
        val targetZ: Int
    }

    interface ThrustMode : AutoFlightMode
    interface VerticalMode : AutoFlightMode
    interface LateralMode : AutoFlightMode

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("auto_flight")
    }
}
