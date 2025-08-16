package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.SpeedReferenceVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.SpeedThrustMode

class SpeedDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showSpeedReading || FAConfig.display.showSpeedScale
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            if (FAConfig.display.showSpeedReading) {
                renderSpeedReading(HudFrame.leftF, centerYF)
            }
            if (FAConfig.display.showSpeedScale) {
                renderSpeedScale(HudFrame.left, centerY)
            }
            if (FAConfig.display.showAutomationModes) {
                renderSpeedTarget(HudFrame.left, HudFrame.top - 9)
            }
        }
    }

    private fun GuiGraphics.renderSpeedReading(x: Float, y: Float) {
        pose().push()
        fusedTranslateScale(x * 1.005f, y, READING_MATRIX_SCALE)

        val speed: Double = computers.hudData.lerpedForwardVelocity.length() * 20
        val color: Int =
            if (speed <= 0.0) warningColor
            else primaryColor

        val text: String = speed.roundToInt().toString()
        val width: Int = textWidth(text) + 5
        val halfHeight = 6
        val textY: Int = -4

        renderOutline(-width, -halfHeight, width, halfHeight * 2 - 1, color)
        drawRightAlignedString(text, -2, textY, color)

        pose().pop()
    }

    private fun GuiGraphics.renderSpeedScale(x: Int, y: Int) {
        val speed: Double = computers.hudData.lerpedForwardVelocity.length() * 20
        val color: Int =
            if (speed <= 0.0) warningColor
            else primaryColor

        val minY: Int = HudFrame.top
        val maxY: Int = (y + lineHeight * (speed + 1)).toInt().coerceIn(minY - 1..HudFrame.bottom)

        vLine(x, minY, maxY, color)

        enableScissor(0, minY, guiWidth(), maxY + 1)

        enableScissor(0, minY, guiWidth(), (if (FAConfig.display.showSpeedReading) y - 6 * READING_MATRIX_SCALE else maxY).toInt() + 1)
        hLine(x - 30, x, y, color)
        hLine(x - 35, x, minY, color)
        for (i: Int in speed.roundToInt()..speed.roundToInt() + 100) {
            if (!drawSpeedLine(x, y, i, speed, color)) {
                break
            }
        }
        disableScissor()

        enableScissor(
            0,
            (if (FAConfig.display.showSpeedReading) y + 5 * READING_MATRIX_SCALE else minY).toInt(),
            guiWidth(),
            maxY + 1
        )
        hLine(x - 35, x, maxY, color)
        for (i: Int in speed.roundToInt() downTo 0) {
            if (!drawSpeedLine(x, y, i, speed, color)) {
                break
            }
        }
        disableScissor()

        disableScissor()
    }

    private fun GuiGraphics.drawSpeedLine(x: Int, y: Int, speed: Int, currentSpeed: Double, color: Int): Boolean {
        val textY: Int = (y + lineHeight * (currentSpeed - speed)).toInt()
        if (textY < HudFrame.top - 100 || textY > HudFrame.bottom + 100) {
            return false
        }
        hLine(x - 5, x, textY, color)
        if (speed % 5 == 0) {
            drawRightAlignedString(speed.toString(), x - 6, textY - 3, color)
        }

        return true
    }

    private fun GuiGraphics.renderSpeedTarget(x: Int, y: Int) {
        val color: Int
        val active: AutoFlightComputer.ThrustMode? = computers.autoflight.activeThrustMode
        if (computers.autoflight.getThrustInput() != null && active is SpeedThrustMode) {
            color = if (active == computers.autoflight.selectedThrustMode) primaryAdvisoryColor else secondaryAdvisoryColor
            drawRightAlignedString(active.target.toString(), x, y, color)
        } else {
            val active: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
            if (computers.autoflight.getPitchInput() != null && active is SpeedReferenceVerticalMode) {
                color = if (active == computers.autoflight.selectedVerticalMode) primaryAdvisoryColor else secondaryAdvisoryColor
                drawRightAlignedString(active.targetSpeed.toString(), x, y, color)
            }
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawRightAlignedString(
                Component.translatable("short.flightassistant.speed"),
                HudFrame.left, centerY - 5, warningColor
            )
        }
    }

    companion object {
        @Deprecated("Increase GUI scale by 1 instead")
        private const val READING_MATRIX_SCALE: Float = 1.5f
        val ID: ResourceLocation = FlightAssistant.id("speed")
    }
}
