package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class ElytraDurabilityDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showElytraDurability
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = (HudFrame.left + (HudFrame.width - HudFrame.height) * 0.25f).toInt()
            val y: Int = HudFrame.bottom + 1

            val text: Component =
                computers.elytra.formatDurability(FAConfig.display.elytraDurabilityUnits, computers.data.player)
                    ?: return

            val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player)!!
            val color: Int = when {
                remainingFlightTime < 30 -> warningColor
                remainingFlightTime < 90 -> cautionColor
                else -> primaryColor
            }

            drawRightAlignedString(Component.translatable("short.flightassistant.elytra"), x - 15, y + 2, color)
            renderOutline(x - 14, y, 29, 11, color)
            drawMiddleAlignedString(text, x, y + 2, color)
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = (HudFrame.left + (HudFrame.width - HudFrame.height) * 0.25f).toInt()
            val y: Int = HudFrame.bottom + 1
            drawMiddleAlignedString(Component.translatable("short.flightassistant.elytra_durability"), x, y, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("elytra_durability")
    }
}
