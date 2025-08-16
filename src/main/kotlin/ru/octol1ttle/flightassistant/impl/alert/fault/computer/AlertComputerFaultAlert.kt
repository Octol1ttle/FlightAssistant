package ru.octol1ttle.flightassistant.impl.alert.fault.computer

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString

class AlertComputerFaultAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 15
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.alert.alertsFaulted
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.alert.fault"), firstLineX, firstLineY, cautionColor)
    }
}
