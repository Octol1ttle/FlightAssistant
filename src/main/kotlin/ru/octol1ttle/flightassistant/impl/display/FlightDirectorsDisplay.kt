package ru.octol1ttle.flightassistant.impl.display

import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.FloatLerper
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class FlightDirectorsDisplay(computers: ComputerBus) : Display(computers) {
    private val pitchTargetLerper: FloatLerper = FloatLerper()
    private val headingTargetLerper: FloatLerper = FloatLerper()
    
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightDirectors
    }

    override fun render(guiGraphics: GuiGraphics) {
        if (!computers.autoflight.flightDirectors || computers.hudData.isViewMirrored) {
            return
        }

        with(guiGraphics) {
            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()

            pose().push()
//? if <1.21.6
            pose().translate(0.0f, 0.0f, -100.0f)
//? if >=1.21.6 {
            /*pose().rotateAbout(ru.octol1ttle.flightassistant.api.util.radians(-computers.hudData.roll), centerXF, centerYF)
*///?} else
            pose().rotateAround(Axis.ZN.rotationDegrees(computers.hudData.roll), centerXF, centerYF, 0.0f)

            enableScissor(HudFrame.left, HudFrame.top, HudFrame.right, HudFrame.bottom)

            val pitchInput: ControlInput? = computers.pitch.activeInput
            val pitchTarget: Float? = pitchTargetLerper.get(pitchInput?.target, FATickCounter.timePassed * 1.5f)
            if (pitchTarget != null && pitchInput != null && pitchInput.priority >= ControlInput.Priority.NORMAL) {
                ScreenSpace.getY(pitchTarget)?.let {
                    hLine(this.centerX - halfWidth, this.centerX + halfWidth, it.coerceIn(HudFrame.top + 1..<HudFrame.bottom - 1), primaryAdvisoryColor)
                }
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            val headingTarget: Float? = headingTargetLerper.get(headingInput?.target, FATickCounter.timePassed * 1.5f)
            if (headingTarget != null && headingInput != null && headingInput.priority >= ControlInput.Priority.NORMAL) {
                ScreenSpace.getX(headingTarget)?.let {
                    vLine(it.coerceIn(HudFrame.left + 1..<HudFrame.right - 1), this.centerY - halfWidth, this.centerY + halfWidth, primaryAdvisoryColor)
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
