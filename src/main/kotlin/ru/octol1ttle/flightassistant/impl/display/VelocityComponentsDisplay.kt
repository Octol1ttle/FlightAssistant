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

class VelocityComponentsDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showGroundSpeed || FAConfig.display.showVerticalSpeed
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.right - 5
            var y: Int = HudFrame.bottom - 10

            if (FAConfig.display.showVerticalSpeed) {
                val verticalSpeed: Double = computers.hudData.lerpedVelocity.perSecond().y
                drawRightAlignedString(
                    Component.translatable(
                        "short.flightassistant.vertical_speed",
                        ": ${verticalSpeed.roundToInt()}"
                    ), x, y, if (verticalSpeed <= -10) warningColor else primaryColor
                )
                y -= lineHeight
            }
            if (FAConfig.display.showGroundSpeed) {
                drawRightAlignedString(
                    Component.translatable(
                        "short.flightassistant.ground_speed",
                        ": ${computers.hudData.lerpedVelocity.perSecond().horizontalDistance().roundToInt()}"
                    ), x, y, primaryColor
                )
            }
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.right - 25
            var y: Int = HudFrame.bottom - 10

            if (FAConfig.display.showVerticalSpeed) {
                drawString(Component.translatable("short.flightassistant.vertical_speed", ""), x, y, warningColor)
                y -= lineHeight
            }
            if (FAConfig.display.showGroundSpeed) {
                drawString(Component.translatable("short.flightassistant.ground_speed", ""), x, y, warningColor)
            }
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("velocity_components")
    }
}
