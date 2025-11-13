package ru.octol1ttle.flightassistant.impl.display

import java.util.Objects
import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FAKeyMappings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AutomationModesDisplay(computers: ComputerBus) : Display(computers) {
    private val thrustDisplay: ModeDisplay = ModeDisplay(1)
    private val pitchDisplay: ModeDisplay = ModeDisplay(2)
    private val headingDisplay: ModeDisplay = ModeDisplay(3)
    private val automationStatusDisplay: ModeDisplay = ModeDisplay(4)

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAutomationModes
    }

    override fun render(guiGraphics: GuiGraphics) {
        renderThrustMode(guiGraphics)
        renderPitchMode(guiGraphics)
        renderInput(guiGraphics, headingDisplay, computers.heading.activeInput)
        renderAutomaticsMode(guiGraphics)
    }

    private fun renderThrustMode(guiGraphics: GuiGraphics) {
        val thrustUnusable: Boolean = computers.thrust.noThrustSource || computers.thrust.reverseUnsupported

        val input: ControlInput? = computers.thrust.activeInput
        if ((input != null && FAKeyMappings.isHoldingThrust()) || FAKeyMappings.globalAutomationOverride.isDown) {
            thrustDisplay.render(guiGraphics, Component.translatable("mode.flightassistant.thrust.override").setColor(cautionColor), ControlInput.Status.ACTIVE, cautionColor)
            return
        }

        if (input != null) {
            thrustDisplay.render(guiGraphics, input.text, input.status,
                if (thrustUnusable || input.status == ControlInput.Status.ACTIVE && input.priority < ControlInput.Priority.NORMAL) cautionColor else null)
            return
        }

        if (computers.thrust.thrustLocked) {
            thrustDisplay.render(
                guiGraphics,
                if (computers.thrust.current == 1.0f) Component.translatable("mode.flightassistant.thrust.locked_toga").setColor(primaryColor)
                else Component.translatable("mode.flightassistant.thrust.locked").setColor(primaryColor),
                ControlInput.Status.ACTIVE,
                if (FATickCounter.totalTicks % 20 >= 10) cautionColor else emptyColor
            )

            return
        }

        if (computers.thrust.current != 0.0f) {
            thrustDisplay.render(
                guiGraphics,
                if (computers.thrust.current == 1.0f) Component.translatable("mode.flightassistant.thrust.manual.toga").setColor(secondaryColor)
                else if (computers.thrust.current < 0.0f) Component.translatable("mode.flightassistant.thrust.manual.reverse")
                else Component.translatable("mode.flightassistant.thrust.manual"),
                ControlInput.Status.ACTIVE, if (thrustUnusable) cautionColor else null
            )
            return
        }

        thrustDisplay.render(guiGraphics, null, ControlInput.Status.ACTIVE)
    }

    private fun renderPitchMode(guiGraphics: GuiGraphics) {
        if (FAKeyMappings.globalAutomationOverride.isDown) {
            pitchDisplay.render(guiGraphics, Component.translatable("mode.flightassistant.vertical.override").setColor(cautionColor), ControlInput.Status.ACTIVE, cautionColor)
            return
        }
        renderInput(guiGraphics, pitchDisplay, computers.pitch.activeInput)
    }

    private fun renderInput(guiGraphics: GuiGraphics, display: ModeDisplay, input: ControlInput?) {
        if (input != null) {
            display.render(guiGraphics, input.text, input.status, if (input.status == ControlInput.Status.ACTIVE && input.priority < ControlInput.Priority.NORMAL) cautionColor else null)
        } else {
            display.render(guiGraphics, null, ControlInput.Status.ACTIVE)
        }
    }

    private fun renderAutomaticsMode(guiGraphics: GuiGraphics) {
        val text: MutableComponent = Component.empty()
        if (computers.autoflight.flightDirectors) {
            text.appendWithSeparation(Component.translatable("short.flightassistant.flight_directors_alt"))
        }
        if (computers.autoflight.autoThrust) {
            text.appendWithSeparation(Component.translatable("short.flightassistant.auto_thrust"))
        }
        if (computers.autoflight.autopilot) {
            text.appendWithSeparation(Component.translatable("short.flightassistant.autopilot"))
        }

        val color =
            if (computers.autoflight.autopilotAlert) warningColor
            else if (computers.autoflight.autoThrustAlert) cautionColor
            else null
        automationStatusDisplay.render(
            guiGraphics,
            if (text.siblings.isNotEmpty() || color != null) text else null, ControlInput.Status.ACTIVE,
            if (FATickCounter.totalTicks % 20 >= 10 || color == null) color else emptyColor
        )
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = centerX
            val y: Int = HudFrame.top - 9

            drawMiddleAlignedString(Component.translatable("short.flightassistant.automation_modes"), x, y, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("automation_modes")
        private const val TOTAL_MODES: Float = 4.0f
    }

    class ModeDisplay(private val order: Int) {
        private var lastText: Component? = null
        private var textChangedAt: Int = 0

        fun render(guiGraphics: GuiGraphics, text: Component?, status: ControlInput.Status = ControlInput.Status.ACTIVE, borderColor: Int? = null) {
            val farLeft: Int = HudFrame.left + 1
            val farRight: Int = HudFrame.right - 1
            val farWidth: Int = farRight - farLeft

            val singleWidth: Int = ((farWidth - (TOTAL_MODES - 1)) / TOTAL_MODES).roundToInt()
            val leftX: Int = farLeft + (singleWidth + 1) * (order - 1)
            val rightX: Int = if (order == TOTAL_MODES.toInt()) farRight + 1 else leftX + singleWidth

            val y: Int = HudFrame.top - 9

            if (status == ControlInput.Status.ACTIVE && !Objects.equals(text, lastText)) {
                textChangedAt = FATickCounter.totalTicks
                lastText = text
            }

            if (text != null) {
                val color = when (status) {
                    ControlInput.Status.ACTIVE -> primaryColor
                    ControlInput.Status.ARMED -> primaryAdvisoryColor
                    ControlInput.Status.UNAVAILABLE -> cautionColor
                    ControlInput.Status.DISABLED -> return
                }
                guiGraphics.drawMiddleAlignedString(text, (leftX + rightX) / 2, y, color)
                if (borderColor != null || FATickCounter.totalTicks <= textChangedAt + 100) {
                    guiGraphics.renderOutline(leftX, y - 2, rightX - leftX, 11, borderColor ?: secondaryColor)
                }
            }
        }
    }
}
