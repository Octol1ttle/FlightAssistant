package ru.octol1ttle.flightassistant.impl.display

import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.config.FAConfig

class FlightDirectorsDisplay(computers: ComputerBus) : Display(computers) {
    private var lastPitch: Float? = null
    private var lastYaw: Float? = null

    override fun allowedByConfig(): Boolean {
        return FAConfig.hud.showFlightDirectors.allowed()
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
            HudFrame.maybeScissor(this)

            val pitchInput: ControlInput? = computers.pitch.activeInput
            if (pitchInput != null && pitchInput.priority >= ControlInput.Priority.NORMAL) {
                val last = lastPitch
                val target = if (last != null) Mth.lerp(FATickCounter.partialTick, last, pitchInput.target) else pitchInput.target
                ScreenSpace.getY(target)?.let {
                    hLine(this.centerX - halfWidth, this.centerX + halfWidth, it, primaryAdvisoryColor)
                }
            }
            if (FATickCounter.ticksPassed >= 1) {
                lastPitch = pitchInput?.target
            }

            val headingInput: ControlInput? = computers.heading.activeInput
            if (headingInput != null && headingInput.priority >= ControlInput.Priority.NORMAL) {
                val last = lastYaw
                val target =
                    if (last == null) headingInput.target
                    else Mth.lerp(FATickCounter.partialTick, last, last + findShortestPath(last, headingInput.target, 360.0f))
                ScreenSpace.getX(target)?.let {
                    vLine(it, this.centerY - halfWidth, this.centerY + halfWidth, primaryAdvisoryColor)
                }
            }
            if (FATickCounter.ticksPassed >= 1) {
                lastYaw = headingInput?.target
            }

            HudFrame.maybeDisableScissor(this)
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
