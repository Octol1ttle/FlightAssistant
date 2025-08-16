package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

class ComputerFaultAlert(
    computers: ComputerBus,
                         private val identifier: ResourceLocation,
                         private val alertText: Component,
                         private val extraTexts: Collection<Component>? = null,
                         override val data: AlertData = AlertData.MASTER_CAUTION
): Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 25

    override fun shouldActivate(): Boolean {
        return ComputerHost.isFaulted(identifier)
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        val color: Int = data.colorSupplier.invoke()
        var i = 0
        i += guiGraphics.drawString(alertText, firstLineX, firstLineY, color)
        var y: Int = firstLineY + 11

        if (extraTexts != null) {
            for (text in extraTexts) {
                i += guiGraphics.drawString(text, otherLinesX, y, primaryAdvisoryColor)
                y += 10
            }
        }

        i +=
            if (ComputerHost.getFaultCount(identifier) == 1) {
                guiGraphics.drawString(Component.translatable("alert.flightassistant.fault.computer.reset"), otherLinesX, y, primaryAdvisoryColor)
            } else {
                0
            }
        return i
    }
}
