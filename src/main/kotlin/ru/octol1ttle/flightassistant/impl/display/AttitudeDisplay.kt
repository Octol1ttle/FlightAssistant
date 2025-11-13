package ru.octol1ttle.flightassistant.impl.display

import com.mojang.math.Axis
import kotlin.math.sign
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.FloatLerper
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.DisplayOptions
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

class AttitudeDisplay(computers: ComputerBus) : Display(computers) {
    private val minimumLerper: FloatLerper = FloatLerper()
    private val maximumLerper: FloatLerper = FloatLerper()

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAttitude != DisplayOptions.AttitudeDisplayMode.DISABLED
    }

    override fun render(guiGraphics: GuiGraphics) {
        if (computers.hudData.isViewMirrored) {
            return
        }

        with(guiGraphics) {
            val centerX: Float = centerXF - 0.5f

            pose().push()
//? if <1.21.6
            pose().translate(0.0f, 0.0f, -200.0f)
//? if >=1.21.6 {
            /*pose().rotateAbout(ru.octol1ttle.flightassistant.api.util.radians(-computers.hudData.roll), centerX, centerYF)
*///?} else
            pose().rotateAround(Axis.ZN.rotationDegrees(computers.hudData.roll), centerX, centerYF, 0.0f)

            if (!FAConfig.display.drawPitchOutsideFrame) {
                HudFrame.scissor(this)
            }
            if (FAConfig.display.showAttitude <= DisplayOptions.AttitudeDisplayMode.HORIZON_ONLY) {
                renderHorizon(centerX)
            }
            if (FAConfig.display.showAttitude == DisplayOptions.AttitudeDisplayMode.HORIZON_AND_LADDER) {
                renderPitchBars(centerX)
                renderPitchLimits(centerX)
                drawPitchReferenceMark(47.5f, centerX)
                drawPitchReferenceMark(-2.2f, centerX)
                drawPitchReferenceMark(-38.5f, centerX)
            }
            if (!FAConfig.display.drawPitchOutsideFrame) {
                disableScissor()
            }

            pose().pop()
            if (FAConfig.display.showAutomationModes) {
                renderPitchTarget(this.centerX - 6, centerY - 10)
            }
        }
    }

    private fun GuiGraphics.renderHorizon(centerX: Float) {
        ScreenSpace.getY(0.0f)?.let {
            val color = getPitchBarColor(0.0f)

            val leftXEnd: Int = (centerX - halfWidth * 0.025f).toInt()
            val leftXStart: Int = (leftXEnd - halfWidth * 0.3f).toInt()
            drawRightAlignedString("0", leftXStart - 3, it - 3, color)
            hLine(leftXStart, leftXEnd, it, color)

            val rightXStart: Int = (centerX + halfWidth * 0.025f).toInt()
            val rightXEnd: Int = (rightXStart + halfWidth * 0.3f).toInt()
            hLine(rightXStart, rightXEnd, it, color)
            drawString("0", rightXEnd + 5, it - 3, color)
        }
    }

    private fun GuiGraphics.renderPitchBars(centerX: Float) {
        val step: Int = FAConfig.display.attitudeDegreeStep
        val nextUp: Int = Mth.roundToward(computers.data.pitch.toInt(), step)
        for (i: Int in nextUp..90 step step) {
            drawPitchBar(i, centerX, (ScreenSpace.getY(i.toFloat()) ?: break))
        }

        val nextDown: Int = Mth.quantize(computers.data.pitch.toDouble(), step)
        for (i: Int in nextDown downTo -90 step step) {
            drawPitchBar(i, centerX, (ScreenSpace.getY(i.toFloat()) ?: break))
        }
    }

    private fun GuiGraphics.renderPitchLimits(centerX: Float) {
        val step: Int = FAConfig.display.attitudeDegreeStep / 2

        val arrowText: Component = Component.literal("V")

        val maxInput: ControlInput? = computers.pitch.maximumPitch
        val minInput: ControlInput? = computers.pitch.minimumPitch
        var max: Float = maximumLerper.get(maxInput?.target, FATickCounter.timePassed * 2.0f) ?: 90.0f
        var min: Float = (minimumLerper.get(minInput?.target, FATickCounter.timePassed * 2.0f) ?: -90.0f).coerceAtMost(max)

        while (max <= 180) {
            val y: Int = ScreenSpace.getY(max) ?: break

            drawMiddleAlignedString(arrowText, centerX.toInt(), y - 9, if (maxInput?.status == ControlInput.Status.ACTIVE) warningColor else cautionColor)

            max += step
        }
        while (min >= -180) {
            val y: Int = ScreenSpace.getY(min) ?: break
            pose().push()

            pose().translate(centerX, y.toFloat() /*? if <1.21.6 {*/, 0.0f /*?}*/) // Rotate around the middle of the arrow
//? if >=1.21.6 {
            /*pose().rotate(ru.octol1ttle.flightassistant.api.util.radians(180.0f))
*///?} else
            pose().mulPose(Axis.ZN.rotationDegrees(180.0f)) // Flip upside down
            drawMiddleAlignedString(arrowText, 0, -9, if (minInput?.status == ControlInput.Status.ACTIVE) warningColor else cautionColor)

            pose().pop()
            min -= step
        }
    }

    private fun GuiGraphics.drawPitchReferenceMark(pitch: Float, centerX: Float) {
        val y = ScreenSpace.getY(pitch) ?: return

        val color: Int = getPitchBarColor(pitch)
        val leftXEnd: Int = (centerX - halfWidth * 0.025f).toInt()
        val leftXStart: Int = (leftXEnd - halfWidth * 0.05f).toInt()
        hLineDashed(leftXStart, leftXEnd, y, 2, color)

        val rightXStart: Int = (centerX + halfWidth * 0.025f).toInt()
        val rightXEnd: Int = (rightXStart + halfWidth * 0.05f).toInt()
        hLineDashed(rightXStart, rightXEnd, y, 2, color)
    }

    private fun GuiGraphics.drawPitchBar(pitch: Int, centerX: Float, y: Int) {
        if (pitch == 0) return

        val color: Int = getPitchBarColor(pitch.toFloat())

        val leftXEnd: Int = (centerX - halfWidth * 0.05f).toInt()
        val leftXStart: Int = (leftXEnd - halfWidth * 0.075f).toInt()
        drawRightAlignedString(pitch.toString(), leftXStart - 2, if (pitch > 0) y else y - 4, color)
        vLine(leftXStart, y, y + 5 * pitch.sign, color)
        hLineDashed(leftXStart, leftXEnd, y, if (pitch < 0) 3 else 1, color)

        val rightXStart: Int = (centerX + halfWidth * 0.05f).toInt()
        val rightXEnd: Int = (rightXStart + halfWidth * 0.075f).toInt()
        hLineDashed(rightXStart, rightXEnd, y, if (pitch < 0) 3 else 1, color)
        vLine(rightXEnd, y, y + 5 * pitch.sign, color)
        drawString(pitch.toString(), rightXEnd + 4, if (pitch > 0) y else y - 4, color)
    }

    private fun getPitchBarColor(pitch: Float): Int {
        val min: ControlInput? = computers.pitch.minimumPitch
        val max: ControlInput? = computers.pitch.maximumPitch
        return if (max != null && pitch > max.target)
            if (max.status == ControlInput.Status.ACTIVE) warningColor else cautionColor
        else if (min != null && pitch < min.target)
            if (min.status == ControlInput.Status.ACTIVE) warningColor else cautionColor
        else
            primaryColor
    }

    private fun GuiGraphics.renderPitchTarget(x: Int, y: Int) {
        val active: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if (computers.autoflight.getPitchInput() != null && active is AutoFlightComputer.FollowsPitchMode) {
            drawRightAlignedString("%.1f".formatRoot(active.targetPitch), x, y, primaryAdvisoryColor)
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            drawMiddleAlignedString(Component.translatable("short.flightassistant.attitude"), centerX, centerY - 16, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("attitude")
    }
}
