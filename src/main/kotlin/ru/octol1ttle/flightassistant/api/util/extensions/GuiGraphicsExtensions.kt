package ru.octol1ttle.flightassistant.api.util.extensions

import com.mojang.blaze3d.vertex.PoseStack
import java.awt.Color
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig

internal val font: Font = mc.font

val lineHeight: Int
    get() = font.lineHeight

val GuiGraphics.halfWidth: Float
    get() = guiWidth() * 0.5f

val GuiGraphics.centerXF: Float
    get() = halfWidth

val GuiGraphics.centerX: Int
    get() = centerXF.toInt()

val GuiGraphics.centerYF: Float
    get() = guiHeight() * 0.5f

val GuiGraphics.centerY: Int
    get() = centerYF.toInt()

const val emptyColor: Int = 0

val whiteColor: Int = ChatFormatting.WHITE.color!! or (255 shl 24)

val primaryColor: Int
    get() = FAConfig.display.primaryColor.rgb

val secondaryColor: Int
    get() = FAConfig.display.secondaryColor.rgb

val primaryAdvisoryColor: Int
    get() = FAConfig.display.primaryAdvisoryColor.rgb

val secondaryAdvisoryColor: Int
    get() = FAConfig.display.secondaryAdvisoryColor.rgb

val cautionColor: Int
    get() = FAConfig.display.cautionColor.rgb

val warningColor: Int
    get() = FAConfig.display.warningColor.rgb

/**
 * Translates this graphics' pose and then scales it.
 */
fun GuiGraphics.fusedTranslateScale(x: Float, y: Float, scale: Float) {
    pose().translate(x, y /*? if <1.21.6 {*/, 0.0f /*?}*/)
    pose().scale(scale, scale /*? if <1.21.6 {*/, 1.0f /*?}*/)
}

fun GuiGraphics.hLineDashed(
    x1: Int, x2: Int, y: Int,
    dashCount: Int, color: Int
) {
    val width: Int = x2 - x1
    val segmentCount: Int = dashCount * 2 - 1
    val dashSize: Int = width / segmentCount
    for (i in 0 until segmentCount) {
        if (i % 2 != 0) {
            continue
        }
        val dx1: Int = i * dashSize + x1
        val dx2: Int = if (i == segmentCount - 1) x2 else ((i + 1) * dashSize) + x1
        hLine(dx1, dx2, y, color)
    }
}

fun textWidth(text: String): Int {
    return font.width(text)
}

fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawString(font, text, x, y, color, shadow)
}

fun GuiGraphics.drawRightAlignedString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawString(font, text, x - font.width(text), y, color, shadow)
}

fun GuiGraphics.drawMiddleAlignedString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawString(font, text, x - font.width(text) / 2 + 1, y, color, shadow)
}

fun textWidth(formattedText: FormattedText): Int {
    return font.width(formattedText)
}

fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false): Int {
    drawString(font, text, x, y, color, shadow)
    return 1
}

private fun getContrasting(original: Int): Int {
    val red: Int = original shr 16 and 255
    val green: Int = original shr 8 and 255
    val blue: Int = original shr 0 and 255
    val luma: Double = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0
    return if (luma > 0.5) Color.BLACK.rgb else Color.WHITE.rgb
}

fun GuiGraphics.drawRightAlignedString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawString(font, text, x - textWidth(text), y, color, shadow)
}

fun GuiGraphics.drawMiddleAlignedString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    drawString(font, text, x - textWidth(text) / 2 + 1, y, color, shadow)
}

fun GuiGraphics.drawHighlightedCenteredText(text: Component, x: Int, y: Int, color: Int, highlight: Boolean, shadow: Boolean = false) {
    pose().push()

    if (highlight) {
        val halfWidth: Int = textWidth(text) / 2
        fill(x - halfWidth - 1, y - 1, x + halfWidth + 2, y + 8, color)
//? if <1.21.6
        pose().translate(0.0f, 0.0f, 100.0f)
        drawMiddleAlignedString(text, x, y, getContrasting(color), shadow)
    } else {
        drawMiddleAlignedString(text, x, y, color, shadow)
    }

    pose().pop()
}

fun PoseStack.push() {
    pushPose()
}

fun PoseStack.pop() {
    popPose()
}

fun org.joml.Matrix3x2fStack.push() {
    pushMatrix()
}

fun org.joml.Matrix3x2fStack.pop() {
    popMatrix()
}

//? if >=1.21.9 {
/*fun GuiGraphics.renderOutline(x: Int, y: Int, width: Int, height: Int, color: Int) {
    submitOutline(x, y, width, height, color)
    renderDeferredElements()
}
*///?}