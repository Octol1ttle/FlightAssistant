package ru.octol1ttle.flightassistant.impl.alert.thrust

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor

class ReverseThrustNotSupportedAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 40
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.thrust.reverseUnsupported
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.thrust.reverse_not_supported"), firstLineX, firstLineY, cautionColor)
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.thrust.reverse_not_supported.set_forward"), otherLinesX, firstLineY + 11, primaryAdvisoryColor)
        return i
    }
}
