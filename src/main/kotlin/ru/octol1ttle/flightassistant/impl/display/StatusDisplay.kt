package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.drawRightAlignedString
import ru.octol1ttle.flightassistant.api.util.extensions.secondaryColor
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig

class StatusDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showStatusMessages
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.right - 5
            var y: Int = HudFrame.top + 5

            val texts: Collection<Component> = computers.dispatchQuery(StatusMessageQuery())
            for (text: Component in texts) {
                drawRightAlignedString(text, x, y, secondaryColor)
                y += 10
            }
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawRightAlignedString(Component.translatable("short.flightassistant.status"), HudFrame.right - 5, HudFrame.top + 5, warningColor)
        }
    }

    class StatusMessageQuery : ComputerQuery<Component>()

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("status")
    }
}