package ru.octol1ttle.flightassistant.impl.display

import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

class CoordinatesDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showCoordinates
    }

    override fun render(guiGraphics: FAGuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 5
            val y: Int = HudFrame.bottom - 19

            val xText = "X: ${Mth.floor(computers.hudData.lerpedPosition.x)}${getDirectionSignX(computers.data.heading)}"
            val zText = "Z: ${Mth.floor(computers.hudData.lerpedPosition.z)}${getDirectionSignZ(computers.data.heading)}"
            drawString(xText, x, y, primaryColor)
            drawString(zText, x, y + lineHeight, primaryColor)

            val color: Int
            val active: AutoFlightComputer.LateralMode? = computers.autoflight.activeLateralMode
            if (FAConfig.display.showAutomationModes && (computers.autoflight.flightDirectors || computers.autoflight.autopilot) && active is AutoFlightComputer.FollowsCoordinatesMode) {
                color = if (active == computers.autoflight.selectedLateralMode) primaryAdvisoryColor else secondaryAdvisoryColor
                drawString(active.targetX.toString(), x + textWidth(xText) + 3, y, color)
                drawString(active.targetZ.toString(), x + textWidth(zText) + 3, y + lineHeight, color)
            }
        }
    }

    private fun getDirectionSignX(heading: Float): String {
        if (heading in 30.0..150.0) {
            return " (+)"
        }

        if (heading in 210.0..330.0) {
            return " (-)"
        }

        return ""
    }

    private fun getDirectionSignZ(heading: Float): String {
        if (heading >= 300 || heading <= 60) {
            return " (-)"
        }

        if (heading in 120.0..240.0) {
            return " (+)"
        }

        return ""
    }

    override fun renderFaulted(guiGraphics: FAGuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.bottom - 19

            drawString("X", x, y, warningColor)
            drawString("Z", x, y + lineHeight, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("coordinates")
    }
}
