package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class FlightPathDisplay(computers: ComputerBus) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightPathVector
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val screenSpaceVec: Vector3f = ScreenSpace.getVector3f(computers.hudData.lerpedVelocity, false) ?: return
            val trueX: Float = screenSpaceVec.x
            val trueY: Float = screenSpaceVec.y

            pose().push()
//? if <1.21.6
            pose().translate(0.0f, 0.0f, -150.0f)
            fusedTranslateScale(trueX, trueY, FAConfig.display.flightPathVectorSize)

            val bodySideSize = 3
            vLine(-bodySideSize, -bodySideSize, bodySideSize, primaryColor)
            vLine(bodySideSize, -bodySideSize, bodySideSize, primaryColor)
            hLine(-bodySideSize, bodySideSize, -bodySideSize, primaryColor)
            hLine(-bodySideSize, bodySideSize, bodySideSize, primaryColor)

            val stabilizerSize = 5
            vLine(0, -bodySideSize - stabilizerSize, -bodySideSize, primaryColor)

            val wingSize = 5
            hLine(-bodySideSize - wingSize, -bodySideSize, 0, primaryColor)
            hLine(bodySideSize, bodySideSize + wingSize, 0, primaryColor)

            pose().pop()
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.flight_path"), centerX, centerY + 16, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_path")
    }
}
