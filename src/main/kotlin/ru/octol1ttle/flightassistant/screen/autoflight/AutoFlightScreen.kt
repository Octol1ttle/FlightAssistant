package ru.octol1ttle.flightassistant.screen.autoflight

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.toFloatOrNullWithFallback
import ru.octol1ttle.flightassistant.api.util.extensions.toIntOrNullWithFallback
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.CycleTextOnlyButton
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.components.TextOnlyButton
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox

class AutoFlightScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: TextOnlyButton
    private lateinit var autoThrust: TextOnlyButton
    private lateinit var autopilot: TextOnlyButton

    private lateinit var thrustCycler: CycleTextOnlyButton<AutoFlightScreenState.ThrustMode>
    private lateinit var verticalCycler: CycleTextOnlyButton<AutoFlightScreenState.VerticalMode>
    private lateinit var lateralCycler: CycleTextOnlyButton<AutoFlightScreenState.LateralMode>

    private val refreshableElements: MutableList<GuiEventListener> = ArrayList()

    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        val baseY: Int = this.height / 3

        flightDirectors = this.addRenderableWidget(
            TextOnlyButton(
                this.centerX - 80, baseY - 20, Component.translatable("menu.flightassistant.autoflight.flight_directors.disabled")
            ) {
                computers.autoflight.setFlightDirectors(!computers.autoflight.flightDirectors)
            })
        autoThrust = this.addRenderableWidget(
            TextOnlyButton(
                this.centerX, baseY - 20, Component.translatable("menu.flightassistant.autoflight.auto_thrust.disabled")
            ) {
                computers.autoflight.setAutoThrust(!computers.autoflight.autoThrust, false)
            })
        autopilot = this.addRenderableWidget(
            TextOnlyButton(
                this.centerX + 80, baseY - 20, Component.translatable("menu.flightassistant.autoflight.autopilot.disabled")
            ) {
                computers.autoflight.setAutoPilot(!computers.autoflight.autopilot, false)
            })

        val thrustMode: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(flightDirectors.baseX - 50, baseY, Component.translatable("menu.flightassistant.autoflight.thrust.mode")))
        thrustCycler = this.addRenderableWidget(CycleTextOnlyButton(thrustMode.x + thrustMode.width, thrustMode.y, AutoFlightScreenState.ThrustMode.entries, state.thrustMode) { state.thrustMode = it; refreshEditBoxes() })
        val verticalMode: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(flightDirectors.baseX - 50, baseY + 12, Component.translatable("menu.flightassistant.autoflight.vertical.mode")))
        verticalCycler = this.addRenderableWidget(CycleTextOnlyButton(verticalMode.x + verticalMode.width, verticalMode.y, AutoFlightScreenState.VerticalMode.entries, state.verticalMode) { state.verticalMode = it; refreshEditBoxes() })
        val lateralMode: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(flightDirectors.baseX - 50, baseY + 24, Component.translatable("menu.flightassistant.autoflight.lateral.mode")))
        lateralCycler = this.addRenderableWidget(CycleTextOnlyButton(lateralMode.x + lateralMode.width, lateralMode.y, AutoFlightScreenState.LateralMode.entries, state.lateralMode) { state.lateralMode = it; refreshEditBoxes() })

        refreshEditBoxes()

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        updateButton(flightDirectors, "menu.flightassistant.autoflight.flight_directors", computers.autoflight.flightDirectors)
        updateButton(autoThrust, "menu.flightassistant.autoflight.auto_thrust", computers.autoflight.autoThrust)
        updateButton(autopilot, "menu.flightassistant.autoflight.autopilot", computers.autoflight.autopilot)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun onClose() {
        state.apply(computers.autoflight)
        super.onClose()
    }

    private fun refreshEditBoxes() {
        for (box: GuiEventListener in refreshableElements) {
            this.removeWidget(box)
        }
        refreshableElements.clear()

        val baseX: Int = flightDirectors.baseX - 50
        var baseY: Int = this.height / 3 + 48
        val baseWidth = 36
        val baseHeight = 12

        when (state.thrustMode) {
            AutoFlightScreenState.ThrustMode.SPEED -> {
                val string: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.speed")))
                refreshableElements.add(string)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(string.x + string.width, string.y - 2, baseWidth, baseHeight, state.targetSpeed, { state.targetSpeed = it }, String::toIntOrNullWithFallback) { it >= 0 }))
                baseY += 16
            }

            AutoFlightScreenState.ThrustMode.VERTICAL_PROFILE -> {
                val climbString: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.climb_thrust")))
                refreshableElements.add(climbString)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(climbString.x + climbString.width, climbString.y - 2, baseWidth, baseHeight, state.climbThrustPercent, { state.climbThrustPercent = it }, String::toIntOrNullWithFallback, { it in 0..100 })))
                val descendString: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 16, Component.translatable("menu.flightassistant.autoflight.target.descend_thrust")))
                refreshableElements.add(descendString)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(descendString.x + descendString.width, descendString.y - 2, baseWidth, baseHeight, state.descendThrustPercent, { state.descendThrustPercent = it }, String::toIntOrNullWithFallback, { it in 0..100 })))
                baseY += 16 * 2
            }

            AutoFlightScreenState.ThrustMode.FLIGHT_PLAN -> Unit
        }

        when (state.verticalMode) {
            AutoFlightScreenState.VerticalMode.PITCH -> {
                val string: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.pitch")))
                refreshableElements.add(string)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(string.x + string.width, string.y - 2, baseWidth, baseHeight, state.targetPitch, { state.targetPitch = it }, String::toFloatOrNullWithFallback, { it in -90.0f..90.0f })))
                baseY += 16
            }

            AutoFlightScreenState.VerticalMode.ALTITUDE -> {
                val string: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.altitude")))
                refreshableElements.add(string)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(string.x + string.width, string.y - 2, baseWidth, baseHeight, state.targetAltitude, { state.targetAltitude = it }, String::toIntOrNullWithFallback)))
                baseY += 16
            }

            AutoFlightScreenState.VerticalMode.FLIGHT_PLAN -> Unit
        }

        when (state.lateralMode) {
            AutoFlightScreenState.LateralMode.HEADING -> {
                val string: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.heading")))
                refreshableElements.add(string)
                refreshableElements.add(
                    this.addRenderableWidget(
                        TypeStrictEditBox(
                            string.x + string.width, string.y - 2, baseWidth, baseHeight, state.targetHeading, { state.targetHeading = it },
                            String::toIntOrNullWithFallback, { it in 0..360 })
                    )
                )
            }

            AutoFlightScreenState.LateralMode.COORDINATES -> {
                val string: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.autoflight.target.coordinates")))
                refreshableElements.add(string)
                val xEditBox: TypeStrictEditBox<Int> = this.addRenderableWidget(TypeStrictEditBox(string.x + string.width, string.y - 2, baseWidth + 4, baseHeight, state.targetCoordinatesX, { state.targetCoordinatesX = it }, String::toIntOrNullWithFallback))
                refreshableElements.add(xEditBox)
                refreshableElements.add(this.addRenderableWidget(TypeStrictEditBox(xEditBox.x + xEditBox.width + 4, xEditBox.y, xEditBox.width, xEditBox.height, state.targetCoordinatesZ, { state.targetCoordinatesZ = it }, String::toIntOrNullWithFallback)))
            }

            AutoFlightScreenState.LateralMode.FLIGHT_PLAN -> Unit
        }
    }

    companion object {
        private fun updateButton(button: TextOnlyButton, baseKey: String, status: Boolean) {
            button.message = Component.translatable(baseKey, Component.translatable("menu.flightassistant.autoflight.${if (status) "enabled" else "disabled"}"))
            button.color = if (status) ChatFormatting.GREEN.color!! else ChatFormatting.RED.color!!
        }

        val state: AutoFlightScreenState = AutoFlightScreenState()
    }
}