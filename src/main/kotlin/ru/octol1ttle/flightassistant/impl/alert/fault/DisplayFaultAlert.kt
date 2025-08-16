package ru.octol1ttle.flightassistant.impl.alert.fault

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class DisplayFaultAlert(computers: ComputerBus, val identifier: ResourceLocation) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 45
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return HudDisplayHost.isFaulted(identifier)
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.fault.hud.$identifier"), firstLineX, firstLineY, cautionColor)
        i +=
            if (HudDisplayHost.countFaults(identifier) == 1) {
                guiGraphics.drawString(Component.translatable("alert.flightassistant.fault.hud.reset"), otherLinesX, firstLineY + 11, primaryAdvisoryColor)
            } else {
                0
            }
        return i
    }
}
