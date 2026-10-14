package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import kotlin.math.roundToInt

class AltitudeDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAltitudeReading || FAConfig.display.showAltitudeScale
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            if (FAConfig.display.showAltitudeReading) {
                renderAltitudeReading(HudFrame.rightF, centerYF)
            }
            if (FAConfig.display.showAltitudeScale) {
                renderAltitudeScale(HudFrame.right, centerY)
            }
            if (FAConfig.display.showAutomationModes) {
                renderAltitudeTarget(HudFrame.right + 2, HudFrame.top - 9)
            }
            if (FAConfig.display.showVerticalSpeed) {
                renderVerticalSpeed(HudFrame.right, centerY)
            }
        }
    }

    private fun GuiGraphics.renderAltitudeReading(x: Float, y: Float) {
        pose().push()
        fusedTranslateScale(x, y, READING_MATRIX_SCALE)

        val altitude: Double = computers.hudData.lerpedAltitude
        val text: String = Mth.floor(altitude).toString()

        val width: Int = textWidth(text) + 5
        val halfHeight = 6
        renderOutline(0, -halfHeight, width, halfHeight * 2 - 1, primaryColor)

        val textY: Int = -4
        drawString(text, 3, textY, primaryColor)

        pose().pop()
    }

    private fun GuiGraphics.renderAltitudeScale(x: Int, y: Int) {
        val altitude: Double = computers.hudData.lerpedAltitude

        val minY: Int = HudFrame.top
        val maxY: Int =
            (y + 2 * (altitude - computers.data.level.bottomY + 1)).toInt().coerceIn(minY - 1..HudFrame.bottom)

        vLine(x, minY, maxY, primaryColor)

        enableScissor(0, minY, guiWidth(), maxY + 1)

        val scissorMaxY: Int = (if (FAConfig.display.showAltitudeReading) y - 6 * READING_MATRIX_SCALE else maxY).toInt()
        enableScissor(0, minY, guiWidth(), scissorMaxY + 1)
        hLine(x, x + 30, y, primaryColor)
        hLine(x, x + 35, minY, primaryColor)
        if (maxY < scissorMaxY) {
            hLine(x, x + 35, maxY, primaryColor)
        }
        val altitudeRoundedUp: Int = Mth.roundToward(altitude.roundToInt(), 5)
        for (i: Int in altitudeRoundedUp..altitudeRoundedUp + 1000 step 5) {
            if (!drawAltitudeLine(x, y, i, altitude)) {
                break
            }
        }
        disableScissor()

        enableScissor(0, (if (FAConfig.display.showAltitudeReading) y + 5 * READING_MATRIX_SCALE else minY).toInt(), guiWidth(), maxY + 1)
        hLine(x, x + 35, maxY, primaryColor)
        val altitudeRoundedDown: Int = Mth.quantize(altitude, 5)
        for (i: Int in altitudeRoundedDown downTo (altitudeRoundedDown - 1000).coerceAtLeast(computers.data.level.bottomY) step 5) {
            if (!drawAltitudeLine(x, y, i, altitude)) {
                break
            }
        }
        disableScissor()

        disableScissor()
    }

    private fun GuiGraphics.drawAltitudeLine(x: Int, y: Int, altitude: Int, currentAltitude: Double): Boolean {
        val textY: Int = (y + 2 * (currentAltitude - altitude)).toInt()
        if (textY < HudFrame.top - 100 || textY > HudFrame.bottom + 100) {
            return false
        }
        hLine(x + 5, x, textY, primaryColor)
        if (altitude % 20 == 0) {
            drawString(altitude.toString(), x + 8, textY - 3, primaryColor)
        }

        return true
    }

    private fun GuiGraphics.renderAltitudeTarget(x: Int, y: Int) {
        val color: Int
        val active: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if ((computers.autoflight.flightDirectors || computers.autoflight.autopilot) && active is AutoFlightComputer.FollowsAltitudeMode) {
            color = if (active == computers.autoflight.selectedVerticalMode) primaryAdvisoryColor else secondaryAdvisoryColor
            drawString(active.targetAltitude.toString(), x, y, color)
        }
    }

    private fun GuiGraphics.renderVerticalSpeed(x: Int, y: Int) {
        enableScissor(0, HudFrame.top, guiWidth(), HudFrame.bottom)

        val verticalSpeed = computers.hudData.lerpedVelocity.perSecond().y
        val descentWarning = computers.gpws.groundImpactTime < 10.0 && (verticalSpeed < -10.0 || computers.gpws.groundY == null)
        val color = if (descentWarning) warningColor else whiteColor

        val speedY = (y - 2 * verticalSpeed).toInt()

        val vSpeedInt = verticalSpeed.roundToInt()
        if (vSpeedInt != 0) {
            val textY = (speedY - 4).coerceIn(HudFrame.top, HudFrame.bottom - 7)
            drawRightAlignedString(vSpeedInt.toString(), x - 4, textY, color)
        }

        if (!descentWarning || computers.gpws.groundImpactTime > 5.0 || totalTicks % 20 >= 10) {
            fill(x - 3, y, x - 1, speedY, color)
        }

        disableScissor()
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawString(Component.translatable("short.flightassistant.altitude"), HudFrame.right, centerY - 5, warningColor)
        }
    }

    companion object {
        @Deprecated("Increase GUI scale by 1 instead")
        private const val READING_MATRIX_SCALE: Float = 1.5f
        val ID: ResourceLocation = FlightAssistant.id("altitude")
    }
}
