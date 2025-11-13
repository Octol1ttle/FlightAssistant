package ru.octol1ttle.flightassistant.impl.alert.gpws

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer.FlightPhase

class MinimumsReachedAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData
        get() = AlertData.MINIMUMS_REACHED
    private var reached = false

    override fun shouldActivate(): Boolean {
        if (computers.plan.currentPhase != FlightPhase.LANDING) {
            reached = false
            return false
        }
        if (reached) {
            return true
        }

        val minimums = computers.plan.getMinimums() ?: return false
        reached = computers.data.altitude <= minimums
        return reached
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.gpws.minimums_reached"), firstLineX, firstLineY, cautionColor)
    }
}