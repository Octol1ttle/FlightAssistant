package ru.octol1ttle.flightassistant.impl.alert.firework

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor

class FireworkNoResponseAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_WARNING

    override fun shouldActivate(): Boolean {
        return computers.firework.waitingForResponse && FATickCounter.totalTicks - computers.firework.lastActivationTime >= 30
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.firework.no_response"), firstLineX, firstLineY, warningColor)
    }
}
