package ru.octol1ttle.flightassistant.impl.alert.flight_plan

import kotlin.math.abs
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.level.levelgen.Heightmap
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class ArrivalElevationDisagreeAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        if (computers.plan.currentPhase < FlightPlanComputer.FlightPhase.APPROACH) {
            return false
        }
        val x = computers.plan.arrivalData.coordinatesX
        val z = computers.plan.arrivalData.coordinatesZ
        if (!computers.chunk.isLoaded(x, z)) {
            return false
        }
        val actualElevation: Int = computers.data.level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)
        return abs(actualElevation - computers.plan.arrivalData.elevation) > 2
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.arrival_elevation_disagree"), firstLineX, firstLineY, cautionColor)
        var y = firstLineY + 1

        y += 10
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.arrival_elevation_disagree.glide_slope_unreliable"), otherLinesX, y, primaryAdvisoryColor)
        y += 10
        i += guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.arrival_elevation_disagree.autopilot_do_not_use"), otherLinesX, y, primaryAdvisoryColor)

        return i
    }
}