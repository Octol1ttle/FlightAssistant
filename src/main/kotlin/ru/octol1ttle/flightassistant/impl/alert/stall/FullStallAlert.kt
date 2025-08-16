package ru.octol1ttle.flightassistant.impl.alert.stall

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.SafetyOptions
import ru.octol1ttle.flightassistant.impl.computer.safety.StallComputer

class FullStallAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData = AlertData.FULL_STALL

    override fun shouldActivate(): Boolean {
        return FAConfig.safety.stallAlertMode.warning() && computers.stall.status == StallComputer.Status.FULL_STALL
    }

    override fun getAlertMethod(): SafetyOptions.AlertMethod {
        return FAConfig.safety.stallAlertMethod
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.stall"), guiGraphics.centerX, y, warningColor, totalTicks % 20 >= 10)
        return true
    }
}
