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

class ThrustLockedAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.THRUST_LOCKED

    override fun shouldActivate(): Boolean {
        return computers.thrust.thrustLocked
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.thrust.locked"), firstLineX, firstLineY, cautionColor)
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.thrust.locked.use_keys"), otherLinesX, firstLineY + 11, primaryAdvisoryColor)
        return i
    }
}
