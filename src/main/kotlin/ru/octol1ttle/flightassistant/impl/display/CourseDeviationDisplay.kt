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

class CourseDeviationDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showCourseDeviation
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            renderLateralDeviation()
            renderVerticalDeviation()
        }
    }

    private fun GuiGraphics.renderLateralDeviation() {
        val deviation = (computers.plan.getLateralDeviation(computers.hudData.lerpedPosition) ?: return).coerceIn(-22.5, 22.5)
        val pixelsPerBlock = 2

        val step = 10
        for (i in (-step * 2)..(step * 2) step step) {
            drawMiddleAlignedString("◦", centerX - i * pixelsPerBlock, HudFrame.bottom - 10, secondaryColor)
        }
        renderOutline((centerX - deviation * pixelsPerBlock - 4).roundToInt(), HudFrame.bottom - 11, 9, 9, secondaryAdvisoryColor)
    }

    private fun GuiGraphics.renderVerticalDeviation() {
        val deviation = (computers.plan.getVerticalDeviation(computers.hudData.lerpedPosition) ?: return).coerceIn(-11.25, 11.25)
        val pixelsPerBlock = 4

        val step = 5
        for (i in (-step * 2)..(step * 2) step step) {
            drawRightAlignedString("◦", HudFrame.right - 10, centerY - i * pixelsPerBlock - 4, secondaryColor)
        }
        renderOutline(HudFrame.right - 17, (centerY - deviation * pixelsPerBlock - 5).roundToInt(), 9, 9, secondaryAdvisoryColor)
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.lateral_deviation"), centerX, HudFrame.bottom - 10, warningColor)
            drawRightAlignedString(Component.translatable("short.flightassistant.vertical_deviation"), HudFrame.right - 10, centerY - 5, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("course_deviation")
    }
}