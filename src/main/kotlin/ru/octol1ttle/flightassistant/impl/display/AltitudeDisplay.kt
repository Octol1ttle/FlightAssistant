package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

class AltitudeDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAltitudeReading || FAConfig.display.showAltitudeScale
    }

    override fun render(guiGraphics: FAGuiGraphics) {
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
        }
    }

    private fun FAGuiGraphics.renderAltitudeReading(x: Float, y: Float) {
        pushPose()
        fusedTranslateScale(x, y, READING_MATRIX_SCALE)

        val altitude: Double = computers.hudData.lerpedAltitude
        val text: String = Mth.floor(altitude).toString()

        val width: Int = textWidth(text) + 5
        val halfHeight = 6
        renderOutline(0, -halfHeight, width, halfHeight * 2 - 1, primaryColor)

        val textY: Int = -4
        drawString(text, 3, textY, primaryColor)

        popPose()
    }

    private fun FAGuiGraphics.renderAltitudeScale(x: Int, y: Int) {
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

        if (FAConfig.display.showVerticalSpeed) {
            enableScissor(0, HudFrame.top, guiWidth(), HudFrame.bottom)
            fill(x - 3, y, x - 1, (y - 2 * computers.hudData.lerpedVelocity.perSecond().y).toInt(), secondaryColor)
            disableScissor()
        }
    }

    private fun FAGuiGraphics.drawAltitudeLine(x: Int, y: Int, altitude: Int, currentAltitude: Double): Boolean {
        val textY: Int = (y + 2 * (currentAltitude - altitude)).toInt()
        if (textY < HudFrame.top - 100 || textY > HudFrame.bottom + 100) {
            return false
        }
        if (textY < y - 10 || textY > y + 10) {
            hLine(x + 5, x, textY, primaryColor)
            if (altitude % 20 == 0) {
                drawString(altitude.toString(), x + 8, textY - 3, primaryColor)
            }
        }

        return true
    }

    private fun FAGuiGraphics.renderAltitudeTarget(x: Int, y: Int) {
        val color: Int
        val active: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if ((computers.autoflight.flightDirectors || computers.autoflight.autopilot) && active is AutoFlightComputer.FollowsAltitudeMode) {
            color = if (active == computers.autoflight.selectedVerticalMode) primaryAdvisoryColor else secondaryAdvisoryColor
            drawString(active.targetAltitude.toString(), x, y, color)
        }
    }

    override fun renderFaulted(guiGraphics: FAGuiGraphics) {
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
