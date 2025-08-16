package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class DontSinkAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData
        get() = AlertData.DONT_SINK
    private var age: Int = 0

    override fun shouldActivate(): Boolean {
        if (computers.plan.currentPhase == FlightPlanComputer.FlightPhase.TAKEOFF && !computers.data.fallDistanceSafe && computers.data.velocity.y < 0) {
            age += FATickCounter.ticksPassed
        } else {
            age = 0
        }

        return age >= 20
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.gpws.dont_sink"), guiGraphics.centerX, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }
}