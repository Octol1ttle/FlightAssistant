package ru.octol1ttle.flightassistant.impl.alert.elytra

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig

class ElytraDurabilityCriticalAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_WARNING

    override fun shouldActivate(): Boolean {
        if (!FAConfig.safety.elytraDurabilityAlertMode.warning()) {
            return false
        }
        val remainingFlightTime: Int = computers.elytra.getRemainingFlightTime(computers.data.player) ?: return false
        return remainingFlightTime < 30
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.elytra.critical_durability"), firstLineX, firstLineY, warningColor)
    }
}
