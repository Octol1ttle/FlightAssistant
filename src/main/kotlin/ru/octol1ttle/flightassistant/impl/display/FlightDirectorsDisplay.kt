package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*

class FlightDirectorsDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean = true

    override fun render(guiGraphics: GuiGraphics) {
        if (!computers.autoflight.flightDirectors) {
            return
        }

        with(guiGraphics) {
            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()

            pose().push()
//? if <1.21.6
            pose().translate(0.0f, 0.0f, -100.0f)
            enableScissor(HudFrame.left, HudFrame.top, HudFrame.right, HudFrame.bottom)

            val pitchInput: ControlInput? = computers.pitch.activeInput
            if (pitchInput != null && pitchInput.priority >= ControlInput.Priority.NORMAL) {
                val pitchY: Int? = ScreenSpace.getY(pitchInput.target, false)
                if (pitchY != null) {
                    hLine(this.centerX - halfWidth, this.centerX + halfWidth, pitchY, primaryAdvisoryColor)
                }
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            if (headingInput != null && headingInput.priority >= ControlInput.Priority.NORMAL) {
                val headingX: Int? = ScreenSpace.getX(headingInput.target, false)
                if (headingX != null) {
                    vLine(headingX, this.centerY - halfWidth, this.centerY + halfWidth, primaryAdvisoryColor)
                }
            }

            disableScissor()
            pose().pop()
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.flight_directors"), centerX, HudFrame.top + 30, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_directors")
    }
}
