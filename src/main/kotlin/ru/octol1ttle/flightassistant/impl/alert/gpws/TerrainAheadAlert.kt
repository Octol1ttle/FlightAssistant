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
import ru.octol1ttle.flightassistant.impl.computer.safety.GroundProximityComputer

class TerrainAheadAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData = AlertData.TERRAIN_AHEAD

    override fun shouldActivate(): Boolean {
        return FAConfig.safety.obstacleAlertMode.caution() && computers.gpws.obstacleImpactStatus == GroundProximityComputer.Status.CAUTION
    }

    override fun getAlertMethod(): SafetyOptions.AlertMethod {
        return FAConfig.safety.obstacleAlertMethod
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.gpws.terrain_ahead"), guiGraphics.centerX, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }
}
