package ru.octol1ttle.flightassistant.api.display

import com.mojang.blaze3d.platform.Window
import net.minecraft.client.gui.GuiGraphics
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig
import kotlin.math.roundToInt

object HudFrame {
    private val window: Window = mc.window
    var width: Float = 0.0f
        private set
    var height: Float = 0.0f
        private set
    var topF: Float = 0.0f
        private set
    var top: Int = 0
        private set
    var bottomF: Float = 0.0f
        private set
    var bottom: Int = 0
        private set
    var leftF: Float = 0.0f
        private set
    var left: Int = 0
        private set
    var rightF: Float = 0.0f
        private set
    var right: Int = 0
        private set

    fun updateDimensions() {
        width = window.guiScaledWidth * FAConfig.hud.frameWidth
        height = window.guiScaledHeight * FAConfig.hud.frameHeight

        topF = ((window.guiScaledHeight - height) * 0.5f)
        bottomF = topF + height
        leftF = ((window.guiScaledWidth - width) * 0.5f)
        rightF = leftF + width

        top = topF.roundToInt()
        bottom = bottomF.toInt()
        left = leftF.roundToInt()
        right = rightF.toInt()
    }

    fun maybeScissor(guiGraphics: GuiGraphics) {
        if (!FAConfig.hud.allowDrawingOutsideFrame) {
            guiGraphics.enableScissor(left, top, right, bottom + 1)
        }
    }

    fun maybeDisableScissor(guiGraphics: GuiGraphics) {
        if (!FAConfig.hud.allowDrawingOutsideFrame) {
            guiGraphics.disableScissor()
        }
    }
}
