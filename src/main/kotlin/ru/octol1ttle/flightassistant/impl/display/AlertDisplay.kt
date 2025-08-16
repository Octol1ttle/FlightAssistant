package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertDisplay(computers: ComputerBus) : Display(computers) {
    private val withUnderline: Style = Style.EMPTY.withUnderlined(true)

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAlerts
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 5
            var y: Int = HudFrame.top + 5

            var renderedCentered = false
            for (category: AlertCategory in computers.alert.categories) {
                val copy: MutableComponent = category.categoryText.copy()
                drawString(copy.setStyle(withUnderline), x, y, (category.getFirstData { it.getAlertMethod().screen() } ?: continue).colorSupplier.invoke())
                copy.append(" ")

                var categoryRendered = false
                var lastRenderedLines = 0
                for (alert: Alert in category.activeAlerts) {
                    if (!alert.getAlertMethod().screen()) {
                        continue
                    }

                    if (!renderedCentered && alert is CenteredAlert) {
                        renderedCentered = alert.render(this, centerY + 8)
                        categoryRendered = categoryRendered || renderedCentered
                        y += lineHeight
                    }

                    if (alert is ECAMAlert) {
                        if (lastRenderedLines > 1) {
                            y += 3
                        }

                        lastRenderedLines = alert.render(this, x + textWidth(copy), x, y)
                        y += lineHeight
                        if (lastRenderedLines > 1) {
                            y += (lineHeight + 1) * (lastRenderedLines - 1)
                        }
                        categoryRendered = true
                    }
                }

                if (!categoryRendered) {
                    y += lineHeight
                }
                y += 3
            }
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 5
            val y: Int = HudFrame.top + 5

            drawString(Component.translatable("short.flightassistant.alert"), x, y, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("alert")
    }
}
