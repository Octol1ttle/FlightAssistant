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

class ObstaclesOnPathAlert(computers: ComputerBus) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.CAUTION_TERRAIN

    override fun shouldActivate(): Boolean {
        computers.plan.waypoints.zipWithNext().forEach { (a, b) ->
            val result = computers.data.level.clip(ClipContext(a.vec3(), b.vec3(), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, computers.data.player))
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