package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.safety.VoidProximityComputer

class RadarAltitudeDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showRadarAltitude
    }

    override fun render(guiGraphics: GuiGraphics) {
        val groundLevel: Double? = computers.gpws.groundY
        if (!computers.chunk.isCurrentLoaded || groundLevel != null && groundLevel > computers.hudData.lerpedAltitude) {
            renderFaulted(guiGraphics)
            return
        }

        with(guiGraphics) {
            val x: Int = HudFrame.right
            val y: Int = HudFrame.bottom + 2

            val altType: MutableComponent
            val altString: String
            val color: Int
            if (groundLevel != null) {
                altType = Component.translatable("short.flightassistant.ground")
                altString = (computers.hudData.lerpedAltitude - groundLevel).roundToInt().toString()
                color = primaryColor
            } else {
                altType = Component.translatable("short.flightassistant.void")
                altString = (computers.hudData.lerpedAltitude - computers.data.voidY).roundToInt().toString()
                color = when (computers.voidProximity.status) {
                    VoidProximityComputer.Status.REACHED_DAMAGE_ALTITUDE -> warningColor
                    VoidProximityComputer.Status.APPROACHING_DAMAGE_ALTITUDE -> cautionColor
                    else -> primaryColor
                }
            }
            val xOffset: Int = textWidth(altType) + 1

            drawString(altType, x - xOffset, y + 2, color)
            renderOutline(x, y, textWidth(altString) + 5, 11, color)
            drawString(altString, x + 3, y + 2, color)
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawString(Component.translatable("short.flightassistant.radar_altitude"), HudFrame.right, HudFrame.bottom + 4, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("radar_altitude")
    }
}
