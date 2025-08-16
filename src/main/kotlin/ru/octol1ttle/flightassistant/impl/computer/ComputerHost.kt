package ru.octol1ttle.flightassistant.impl.computer

import java.util.function.Function
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.api.computer.*
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FireworkComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.HeadingComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.ThrustComputer
import ru.octol1ttle.flightassistant.impl.computer.data.AirDataComputer
import ru.octol1ttle.flightassistant.impl.computer.data.HudDisplayDataComputer
import ru.octol1ttle.flightassistant.impl.computer.safety.*

internal object ComputerHost : ModuleController<Computer>, ComputerBus {
    private val computers: MutableMap<ResourceLocation, Computer> = LinkedHashMap()

    override val modulesResettable: Boolean = true

    override fun identifiers(): Set<ResourceLocation> {
        return computers.keys
    }

    override fun get(identifier: ResourceLocation): Computer {
        return computers[identifier] ?: throw IllegalArgumentException("No computer registered with ID: $identifier")
    }

    override fun isEnabled(identifier: ResourceLocation): Boolean {
        return get(identifier).enabled
    }

    override fun setEnabled(identifier: ResourceLocation, enabled: Boolean): Boolean {
        val computer: Computer = get(identifier)

        val oldEnabled: Boolean = computer.enabled
        computer.enabled = enabled
        computer.reset()
        if (!computer.enabled) {
            computer.faulted = false
        }

        return oldEnabled
    }

    override fun isFaulted(identifier: ResourceLocation): Boolean {
        return get(identifier).faulted
    }

    fun getFaultCount(identifier: ResourceLocation): Int {
        return get(identifier).faultCount
    }

    private fun register(identifier: ResourceLocation, module: Computer) {
        if (FlightAssistant.initComplete) {
            throw IllegalStateException("Initialization is already complete, but trying to register a computer with identifier: $identifier")
        }
        if (computers.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered computer with identifier: $identifier")
        }
        computers[identifier] = module
    }

    private fun registerBuiltin() {
        register(AirDataComputer.ID, AirDataComputer(this, mc))
        register(HudDisplayDataComputer.ID, HudDisplayDataComputer(this, mc))
        register(FlightProtectionsComputer.ID, FlightProtectionsComputer(this))

        register(StallComputer.ID, StallComputer(this))
        register(VoidProximityComputer.ID, VoidProximityComputer(this))
        register(GroundProximityComputer.ID, GroundProximityComputer(this))
        register(ElytraStatusComputer.ID, ElytraStatusComputer(this))
        register(ChunkStatusComputer.ID, ChunkStatusComputer(this))

        register(FlightPlanComputer.ID, FlightPlanComputer(this))
        register(AutoFlightComputer.ID, AutoFlightComputer(this))
        register(FireworkComputer.ID, FireworkComputer(this, mc))
        register(PitchComputer.ID, PitchComputer(this))
        register(HeadingComputer.ID, HeadingComputer(this))
        register(RollComputer.ID, RollComputer(this))
        register(ThrustComputer.ID, ThrustComputer(this))

        register(AlertComputer.ID, AlertComputer(this, mc.soundManager))
    }

    internal fun sendRegistrationEvent() {
        registerBuiltin()
        ComputerRegistrationCallback.EVENT.invoker().register(this, this::register)
        computers.values.forEach(Computer::subscribeToEvents)
        computers.values.forEach(Computer::invokeEvents)

        logRegisterComplete()
    }

    private fun logRegisterComplete() {
        val namespaces = ArrayList<String>()
        for (id: ResourceLocation in computers.keys) {
            if (!namespaces.contains(id.namespace)) {
                namespaces.add(id.namespace)
            }
        }
        FlightAssistant.logger.info(
            "Registered {} computers from mods: {}",
            computers.size,
            namespaces.joinToString(", ")
        )
    }

    internal fun tick(partialTick: Float) {
        val paused: Boolean = mc.isPaused /*? if >=1.21 {*/ /*|| !(mc as ru.octol1ttle.flightassistant.mixin.ClientLevelRunningNormallyInvoker).invokeIsLevelRunningNormally() *///?}
        FATickCounter.tick(mc.player!!, partialTick, paused)
        if (paused || FATickCounter.ticksSinceWorldLoad < FATickCounter.worldLoadWaitTime || !FAConfig.global.modEnabled) {
            return
        }

        for ((id: ResourceLocation, computer: Computer) in computers) {
            if (computer.enabled) {
                try {
                    computer.tick()
                } catch (t: Throwable) {
                    onComputerFault(computer)

                    FlightAssistant.logger.error("Exception ticking computer with identifier: $id", t)
                }
            }
        }
    }

    override fun <C, T> guardedCall(computer: C, call: (C) -> T): T? {
        try {
            return call(computer)
        } catch (t: Throwable) {
            if (computer !is Computer) return null
            onComputerFault(computer)

            FlightAssistant.logger.error("Exception invoking guarded call", t)

            return null
        }
    }

    override fun <Event : ComputerEvent> dispatchEvent(event: Event) {
        for (computer: Computer in computers.values) {
            if (!computer.isDisabledOrFaulted()) {
                guardedCall(computer) { it.processEvent(event) }
            }
        }
    }

    override fun <Response> dispatchQuery(query: ComputerQuery<Response>): Collection<Response> {
        for (computer: Computer in computers.values) {
            if (!computer.isDisabledOrFaulted()) {
                guardedCall(computer) { it.processQuery(query) }
            }
        }

        return query.responses
    }

    private fun onComputerFault(computer: Computer) {
        if (computer.faulted) {
            computer.enabled = false
        }

        computer.faulted = true
        computer.faultCount++
        computer.reset()
    }
}
