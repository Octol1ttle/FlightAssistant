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
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class DepartureElevationDisagreeAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        if (computers.plan.currentPhase != FlightPlanComputer.FlightPhase.TAKEOFF) {
            return false
        }
        val x = computers.plan.departureData.coordinatesX
        val z = computers.plan.departureData.coordinatesZ
        if (!computers.chunk.isLoaded(x, z)) {
            return false
        }
        val actualElevation: Int = computers.data.level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z)
        return abs(actualElevation - computers.plan.departureData.elevation) > 2
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.departure_elevation_disagree"), firstLineX, firstLineY, cautionColor)
    }
}