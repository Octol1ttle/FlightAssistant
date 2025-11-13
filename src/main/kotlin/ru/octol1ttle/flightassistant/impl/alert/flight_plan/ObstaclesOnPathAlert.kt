package ru.octol1ttle.flightassistant.impl.alert.flight_plan

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class ObstaclesOnPathAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.CAUTION_TERRAIN

    override fun shouldActivate(): Boolean {
        if (computers.plan.enrouteData.isEmpty()) {
            return false
        }

        if (!computers.plan.departureData.isDefault()) {
            val result = computers.data.level.clip(ClipContext(computers.plan.departureData.vec3().add(0.0, 3.0, 0.0), computers.plan.enrouteData.first().vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, computers.data.player))
            if (result.type == HitResult.Type.BLOCK) {
                return true
            }
        }

        var lastEnroute: FlightPlanComputer.EnrouteWaypoint? = null
        for (enroute in computers.plan.enrouteData) {
            if (lastEnroute == null) {
                lastEnroute = enroute
                continue
            }

            val result = computers.data.level.clip(ClipContext(lastEnroute.vec3(), enroute.vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, computers.data.player))

            lastEnroute = enroute
            if (result.type == HitResult.Type.BLOCK) {
                return true
            }
        }

        if (!computers.plan.arrivalData.isDefault()) {
            val result = computers.data.level.clip(ClipContext(computers.plan.enrouteData.last().vec3(), computers.plan.arrivalData.vec3().add(0.0, 3.0, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, computers.data.player))
            if (result.type == HitResult.Type.BLOCK) {
                return true
            }
        }

        return false
    }

    override fun render(guiGraphics: GuiGraphics, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return guiGraphics.drawString(Component.translatable("alert.flightassistant.flight_plan.obstacles_on_path"), firstLineX, firstLineY, cautionColor)
    }
}