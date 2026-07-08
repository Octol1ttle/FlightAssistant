package ru.octol1ttle.flightassistant.impl.display

import com.mojang.math.Axis
import kotlin.math.roundToInt
import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics
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
    private val xLerper: FloatLerper = FloatLerper()
    private val yLerper: FloatLerper = FloatLerper()

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightDirectors
    }

    override fun render(guiGraphics: FAGuiGraphics) {
        if (!computers.autoflight.flightDirectors || computers.hudData.isViewMirrored) {
            return
        }

        with(guiGraphics) {
            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()

            pushPose()
//? if <1.21.6
            pose().translate(0.0f, 0.0f, -100.0f)
//? if >=1.21.6 {
            /*pose().rotateAbout(ru.octol1ttle.flightassistant.api.util.radians(-computers.hudData.roll), centerXF, centerYF)
*///?} else
            pose().rotateAround(Axis.ZN.rotationDegrees(computers.hudData.roll), centerXF, centerYF, 0.0f)

            enableScissor(HudFrame.left, HudFrame.top, HudFrame.right, HudFrame.bottom)

            val pitchInput: ControlInput? = computers.pitch.activeInput
            if (pitchInput != null && pitchInput.priority >= ControlInput.Priority.NORMAL) {
                yLerper.get(ScreenSpace.getY(pitchInput.target)?.toFloat(), FATickCounter.timePassed * 1.5f)?.roundToInt()?.let {
                    hLine(this.centerX - halfWidth, this.centerX + halfWidth, it.coerceIn(HudFrame.top + 1, HudFrame.bottom - 2), primaryAdvisoryColor)
                }
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            if (headingInput != null && headingInput.priority >= ControlInput.Priority.NORMAL) {
                xLerper.get(ScreenSpace.getX(headingInput.target)?.toFloat(), FATickCounter.timePassed * 1.5f)?.roundToInt()?.let {
                    vLine(it.coerceIn(HudFrame.left + 1, HudFrame.right - 2), this.centerY - halfWidth, this.centerY + halfWidth, primaryAdvisoryColor)
                }
            }

            disableScissor()
            popPose()
        }
    }

    override fun renderFaulted(guiGraphics: FAGuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.flight_directors"), centerX, HudFrame.top + 30, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_directors")
    }
}
