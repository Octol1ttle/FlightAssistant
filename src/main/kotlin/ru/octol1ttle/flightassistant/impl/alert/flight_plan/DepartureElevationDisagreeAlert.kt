package ru.octol1ttle.flightassistant.impl.alert.flight_plan

import kotlin.math.abs
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class DepartureElevationDisagreeAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return computers.plan.currentPhase == FlightPlanComputer.FlightPhase.TAKEOFF && abs((computers.data.groundY ?: Double.MAX_VALUE) - computers.plan.departureData.elevation) > 5
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.departure_elevation_disagree"), firstLineX, firstLineY, cautionColor)
    }
}