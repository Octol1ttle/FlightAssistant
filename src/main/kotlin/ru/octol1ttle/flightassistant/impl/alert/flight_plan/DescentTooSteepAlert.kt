package ru.octol1ttle.flightassistant.impl.alert.flight_plan

import kotlin.math.asin
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class DescentTooSteepAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        if (computers.plan.enrouteData.isEmpty()) {
            return false
        }

        var lastEnroute: FlightPlanComputer.EnrouteWaypoint? = null
        for (enroute in computers.plan.enrouteData) {
            if (lastEnroute == null) {
                lastEnroute = enroute
                continue
            }

            if (degrees(asin(enroute.vec3().subtract(lastEnroute.vec3()).normalize().y)) < -35.0f) {
                return true
            }
        }

        if (!computers.plan.arrivalData.isDefault()) {
            if (degrees(asin(computers.plan.arrivalData.vec3().subtract(computers.plan.enrouteData.last().vec3()).normalize().y)) < -35.0f) {
                return true
            }
        }

        return false
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.descent_too_steep"), firstLineX, firstLineY, cautionColor)
    }
}