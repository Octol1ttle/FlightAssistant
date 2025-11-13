package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.SafetyOptions
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class BelowGlideSlopeAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData
        get() = AlertData.BELOW_GLIDE_SLOPE

    override fun shouldActivate(): Boolean {
        if (!FAConfig.safety.belowGlideSlopeAlertMode.caution()) {
            return false
        }
        if (computers.plan.currentPhase != FlightPlanComputer.FlightPhase.LANDING) {
            return false
        }

        val glideSlopeDeviation = computers.plan.getVerticalDeviation(computers.data.position)!!
        return glideSlopeDeviation > 5.0
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.gpws.below_glide_slope"), guiGraphics.centerX, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }

    override fun getAlertMethod(): SafetyOptions.AlertMethod {
        return FAConfig.safety.belowGlideSlopeAlertMethod
    }
}