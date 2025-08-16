package ru.octol1ttle.flightassistant.impl.alert.elytra

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.config.FAConfig

class ElytraDurabilityLowAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 30
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        if (!FAConfig.safety.elytraDurabilityAlertMode.caution()) {
            return false
        }
        val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player) ?: return false
        return remainingFlightTime in 30..<90
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.elytra.low_durability"), firstLineX, firstLineY, cautionColor)
    }
}
