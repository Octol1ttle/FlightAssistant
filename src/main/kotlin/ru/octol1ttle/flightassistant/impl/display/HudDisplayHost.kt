package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudDisplayRegistrationCallback
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.centerY
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryColor
import ru.octol1ttle.flightassistant.config.FAConfig

internal object HudDisplayHost: ModuleController<Display> {
    private val displays: MutableMap<ResourceLocation, Display> = HashMap()

    override val modulesResettable: Boolean = false

    override fun get(identifier: ResourceLocation): Display {
        return displays[identifier] ?: throw IllegalArgumentException("No display was found with identifier: $identifier")
    }

    override fun isEnabled(identifier: ResourceLocation): Boolean {
        return get(identifier).enabled
    }

    override fun isFaulted(identifier: ResourceLocation): Boolean {
        return get(identifier).faulted
    }

    override fun setEnabled(identifier: ResourceLocation, enabled: Boolean): Boolean {
        val display: Display = get(identifier)

        val oldEnabled: Boolean = display.enabled
        display.enabled = enabled
        return oldEnabled
    }

    fun countFaults(identifier: ResourceLocation): Int {
        return get(identifier).faultCount
    }

    override fun identifiers(): Set<ResourceLocation> {
        return displays.keys
    }

    private fun register(identifier: ResourceLocation, module: Display) {
        if (FlightAssistant.initComplete) {
            throw IllegalStateException("Initialization is already complete, but trying to register a display with identifier: $identifier")
        }
        if (displays.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered display with identifier: $identifier")
        }

        displays[identifier] = module
    }

    private fun registerBuiltin(computers: ComputerBus) {
        register(AlertDisplay.ID, AlertDisplay(computers))
        register(AltitudeDisplay.ID, AltitudeDisplay(computers))
        register(AttitudeDisplay.ID, AttitudeDisplay(computers))
        register(AutomationModesDisplay.ID, AutomationModesDisplay(computers))
        register(CoordinatesDisplay.ID, CoordinatesDisplay(computers))
        register(ElytraDurabilityDisplay.ID, ElytraDurabilityDisplay(computers))
        register(FlightDirectorsDisplay.ID, FlightDirectorsDisplay(computers))
        register(FlightPathDisplay.ID, FlightPathDisplay(computers))
        register(HeadingDisplay.ID, HeadingDisplay(computers))
        register(RadarAltitudeDisplay.ID, RadarAltitudeDisplay(computers))
        register(SpeedDisplay.ID, SpeedDisplay(computers))
        register(StatusDisplay.ID, StatusDisplay(computers))
        register(VelocityComponentsDisplay.ID, VelocityComponentsDisplay(computers))
    }

    internal fun sendRegistrationEvent(computers: ComputerBus) {
        registerBuiltin(computers)
        HudDisplayRegistrationCallback.EVENT.invoker().register(computers, this::register)
        logRegisterComplete()
    }

    private fun logRegisterComplete() {
        val namespaces = ArrayList<String>()
        for (id: ResourceLocation in displays.keys) {
            if (!namespaces.contains(id.namespace)) {
                namespaces.add(id.namespace)
            }
        }
        FlightAssistant.logger.info(
            "Registered {} displays from mods: {}",
            displays.size,
            namespaces.joinToString(", ")
        )
    }

    fun render(guiGraphics: GuiGraphics) {
        if (!FAConfig.hudEnabled) {
            return
        }

        HudFrame.updateDimensions()
        ScreenSpace.updateViewport()

        for ((id: ResourceLocation, display: Display) in displays.filter { entry -> entry.value.allowedByConfig() }) {
            if (FATickCounter.ticksSinceWorldLoad < FATickCounter.worldLoadWaitTime) {
                with(guiGraphics) {
                    drawMiddleAlignedString(Component.translatable("misc.flightassistant.waiting_for_world_load"), centerX, centerY - 16, primaryColor)
                    drawMiddleAlignedString(Component.translatable("misc.flightassistant.waiting_for_world_load.maximum_time"), centerX, centerY + 8, primaryColor)
                }
                return
            }

            if (!display.enabled || !RenderMatrices.ready) {
                try {
                    display.renderFaulted(guiGraphics)
                } catch (t: Throwable) {
                    FlightAssistant.logger.error("Exception rendering disabled display with identifier: $id", t)
                }
                continue
            }

            try {
                display.render(guiGraphics)
                display.faulted = false
            } catch (t: Throwable) {
                display.faulted = true
                display.faultCount++
                display.enabled = false
                FlightAssistant.logger.error("Exception rendering display with identifier: $id", t)
            }
        }
    }
}
