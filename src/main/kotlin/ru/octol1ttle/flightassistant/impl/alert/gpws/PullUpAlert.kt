package ru.octol1ttle.flightassistant.impl.alert.gpws

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
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

// TODO: should not be the same alert for obstacle impact and ground impact
class PullUpAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData = AlertData.PULL_UP

    override fun shouldActivate(): Boolean {
        val groundImpact: Boolean = FAConfig.safety.sinkRateAlertMode.warning() && computers.gpws.groundImpactStatus <= GroundProximityComputer.Status.WARNING
        val obstacleImpact: Boolean = FAConfig.safety.obstacleAlertMode.warning() && computers.gpws.obstacleImpactStatus <= GroundProximityComputer.Status.WARNING
        return groundImpact || obstacleImpact
    }

    override fun getAlertMethod(): SafetyOptions.AlertMethod {
        return if (computers.gpws.groundImpactStatus < computers.gpws.obstacleImpactStatus) {
            FAConfig.safety.sinkRateAlertMethod
        } else if (computers.gpws.groundImpactStatus == computers.gpws.obstacleImpactStatus) {
            SafetyOptions.AlertMethod.min(FAConfig.safety.sinkRateAlertMethod, FAConfig.safety.obstacleAlertMethod)
        } else {
            FAConfig.safety.obstacleAlertMethod
        }
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        val flash: Boolean =
            if (computers.gpws.groundImpactStatus == GroundProximityComputer.Status.RECOVER
                || computers.gpws.obstacleImpactStatus == GroundProximityComputer.Status.RECOVER) totalTicks % 10 >= 5
            else totalTicks % 20 >= 10
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.gpws.pull_up"), guiGraphics.centerX, y, warningColor, flash)
        return true
    }
}
