package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.HeadingLateralMode

class HeadingDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showHeadingReading || FAConfig.display.showHeadingScale
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            if (FAConfig.display.showHeadingReading) {
                renderHeadingReading()
            }
            if (FAConfig.display.showHeadingScale) {
                renderHeadingScale(centerX, HudFrame.bottom + 1)
            }
            if (FAConfig.display.showAutomationModes) {
                renderHeadingTarget(centerX, HudFrame.bottom - 8)
            }
        }
    }

    private fun GuiGraphics.renderHeadingReading() {
        val x: Int = centerX
        val y: Int = HudFrame.bottom + 1

        val headingInt: Int = computers.data.heading.roundToInt()

        renderOutline(x - 11, y, 23, 11, primaryColor)
        drawMiddleAlignedString("%03d".format(headingInt), x, y + 2, primaryColor)
    }

    private fun GuiGraphics.renderHeadingScale(x: Int, y: Int) {
        val left: Int = (x - HudFrame.height * 0.5f).toInt()
        val right: Int = (x + HudFrame.height * 0.5f).toInt()

        hLine(left, right, y, primaryColor)
        vLine(left, y, y + 20, primaryColor)
        vLine(right, y, y + 20, primaryColor)

        enableScissor(left, 0, x - 12, guiHeight())
        val headingRoundedDown: Int = Mth.quantize(computers.data.heading.toDouble(), 10)
        for (i: Int in headingRoundedDown downTo -360 step 10) {
            if (!drawHeadingLine(x, y, left, right, i, computers.data.heading, true)) {
                break
            }
        }
        disableScissor()

        enableScissor(x + 13, 0, right, guiHeight())
        val headingRoundedUp: Int = Mth.roundToward(computers.data.heading.roundToInt(), 10)
        for (i: Int in headingRoundedUp..720 step 10) {
            if (!drawHeadingLine(x, y, left, right, i, computers.data.heading, false)) {
                break
            }
        }
        disableScissor()
    }

    private fun GuiGraphics.drawHeadingLine(x: Int, y: Int, left: Int, right: Int, heading: Int, currentHeading: Float, isLeft: Boolean): Boolean {
        val textX: Int = (x + 2 * findShortestPath(currentHeading, heading.toFloat(), 360.0f)).toInt()
        if (textX < left - 100 || textX > right + 100) {
            return false
        }

        val wrappedHeading: Int = if (heading > 0) heading % 360 else 360 + heading % 360
        vLine(textX, y, y + 3, primaryColor)
        if (wrappedHeading % 30 == 0) {
            drawMiddleAlignedString((if (wrappedHeading == 0) 360 else wrappedHeading).toString(), textX, y + 4, primaryColor)
        }

        if (wrappedHeading % 90 == 0) {
            disableScissor()
            enableScissor(left, 0, right, guiHeight())
            drawMiddleAlignedString(
                when (wrappedHeading) {
                    0, 360 -> "Z-"
                    90 -> "X+"
                    180 -> "Z+"
                    270 -> "X-"
                    else -> throw IllegalArgumentException("Degree range out of bounds: $heading")
                }, textX, y + 12, primaryColor
            )
            disableScissor()
            if (isLeft) {
                enableScissor(left, 0, x - 12, guiHeight())
            } else {
                enableScissor(x + 13, 0, right, guiHeight())
            }
        }

        return true
    }

    private fun GuiGraphics.renderHeadingTarget(x: Int, y: Int) {
        val active: AutoFlightComputer.LateralMode? = computers.autoflight.activeLateralMode
        if (computers.autoflight.getHeadingInput() != null && active is HeadingLateralMode) {
            drawMiddleAlignedString("%03d".format(active.target), x, y, primaryAdvisoryColor)
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.heading"), centerX, HudFrame.bottom + 1, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("heading")
    }
}
