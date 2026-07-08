package ru.octol1ttle.flightassistant.api.util.extensions

import com.mojang.blaze3d.vertex.PoseStack
import java.awt.Color
import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import ru.octol1ttle.flightassistant.api.util.extensions.FAGuiGraphics
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig

internal val font: Font = mc.font

val lineHeight: Int
    get() = font.lineHeight

val FAGuiGraphics.halfWidth: Float
    get() = guiWidth() * 0.5f

val FAGuiGraphics.centerXF: Float
    get() = halfWidth

val FAGuiGraphics.centerX: Int
    get() = centerXF.toInt()

val FAGuiGraphics.centerYF: Float
    get() = guiHeight() * 0.5f

val FAGuiGraphics.centerY: Int
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
fun FAGuiGraphics.fusedTranslateScale(x: Float, y: Float, scale: Float) {
    pose().translate(x, y /*? if <1.21.6 {*/, 0.0f /*?}*/)
    pose().scale(scale, scale /*? if <1.21.6 {*/, 1.0f /*?}*/)
}

fun FAGuiGraphics.hLineDashed(
    x1: Int, x2: Int, y: Int,
    dashCount: Int, color: Int
) {
    val width = x2 - x1
    if (width <= dashCount) {
        hLine(x1, x2, y, color)
        return
    }
    val spaces = dashCount - 1
    // the actual space width is (spaceOffset - 1)
    var spaceOffset = max(2, width / (dashCount * 2))
    while ((width - spaces * spaceOffset) % dashCount != 0) {
        spaceOffset++
    }
    val singleWidth = (width - spaces * spaceOffset) / dashCount
    for (i in 0..<dashCount) {
        val fromLastDash = (singleWidth + spaceOffset) * i
        hLine(x1 + fromLastDash, x1 + fromLastDash + singleWidth, y, color)
    }
}

fun textWidth(text: String): Int {
    return font.width(text)
}

fun FAGuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
//? if <26.2 {
    drawString(font, text, x, y, color, shadow)
//?} else {
  this.text(font, text, x, y, color, shadow)
//?}
}

fun FAGuiGraphics.drawRightAlignedString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
//? if <26.2 {
    drawString(font, text, x - font.width(text), y, color, shadow)
//?} else {
  this.text(font, text, x - font.width(text), y, color, shadow)
//?}
}

fun FAGuiGraphics.drawMiddleAlignedString(text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
//? if <26.2 {
    drawString(font, text, x - font.width(text) / 2 + 1, y, color, shadow)
//?} else {
  this.text(font, text, x - font.width(text) / 2 + 1, y, color, shadow)
//?}
}

fun textWidth(formattedText: FormattedText): Int {
    return font.width(formattedText)
}

fun FAGuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false): Int {
//? if <26.2 {
    drawString(font, text, x, y, color, shadow)
//?} else {
  this.text(font, text, x, y, color, shadow)
//?}
    return 1
}

private fun getContrasting(original: Int): Int {
    val red: Int = original shr 16 and 255
    val green: Int = original shr 8 and 255
    val blue: Int = original shr 0 and 255
    val luma: Double = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0
    return if (luma > 0.5) Color.BLACK.rgb else Color.WHITE.rgb
}

fun FAGuiGraphics.drawRightAlignedString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
//? if <26.2 {
    drawString(font, text, x - textWidth(text), y, color, shadow)
//?} else {
  this.text(font, text, x - textWidth(text), y, color, shadow)
//?}
}

fun FAGuiGraphics.drawMiddleAlignedString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
//? if <26.2 {
    drawString(font, text, x - textWidth(text) / 2 + 1, y, color, shadow)
//?} else {
  this.text(font, text, x - textWidth(text) / 2 + 1, y, color, shadow)
//?}
}

fun FAGuiGraphics.drawHighlightedCenteredText(text: Component, x: Int, y: Int, color: Int, highlight: Boolean, shadow: Boolean = false) {
    pushPose()

    if (highlight) {
        val halfWidth: Int = textWidth(text) / 2
        fill(x - halfWidth - 1, y - 1, x + halfWidth + 2, y + 8, color)
//? if <1.21.6
        pose().translate(0.0f, 0.0f, 100.0f)
        drawMiddleAlignedString(text, x, y, getContrasting(color), shadow)
    } else {
        drawMiddleAlignedString(text, x, y, color, shadow)
    }

    popPose()
}





fun org.joml.Matrix3x2fStack.push() {
    pushMatrix()
}


//? if <26.2 {
//? if >=1.21.9 {
/*fun FAGuiGraphics.renderOutline(x: Int, y: Int, width: Int, height: Int, color: Int) {
//? if >=1.21.11 {
    /^renderOutline(
^///?} else
    submitOutline(
        x, y, width, height, color)
    renderDeferredElements()
}
*///?}
//?}


//? if >=26.2 {
fun FAGuiGraphics.renderOutline(x: Int, y: Int, width: Int, height: Int, color: Int) {
    fill(x, y, x + width, y + 1, color)
    fill(x, y + height - 1, x + width, y + height, color)
    fill(x, y + 1, x + 1, y + height - 1, color)
    fill(x + width - 1, y + 1, x + width, y + height - 1, color)
}
//?}





//? if >=26.2 {
fun FAGuiGraphics.hLine(minX: Int, maxX: Int, y: Int, color: Int) {
    var minXVar = minX
    var maxXVar = maxX
    if (minX > maxX) {
        minXVar = maxX
        maxXVar = minX
    }
    fill(minXVar, y, maxXVar + 1, y + 1, color)
}

fun FAGuiGraphics.vLine(x: Int, minY: Int, maxY: Int, color: Int) {
    var minYVar = minY
    var maxYVar = maxY
    if (minY > maxY) {
        minYVar = maxY
        maxYVar = minY
    }
    fill(x, minYVar, x + 1, maxYVar + 1, color)
}

val net.minecraft.ChatFormatting.color: Int?
    get() = net.minecraft.network.chat.TextColor.fromLegacyFormat(this)?.value
//?}

@Suppress("UNCHECKED_CAST")
fun <T> castToPlatformType(obj: Any?): T = obj as T

fun net.minecraft.client.Minecraft.setScreenSafe(screen: net.minecraft.client.gui.screens.Screen?) {
//? if <26.2 {
    this.setScreen(screen)
//?} else {
    this.setScreenAndShow(castToPlatformType(screen))
//?}
}

//? if >=26.2 {
fun net.minecraft.client.gui.components.AbstractWidget.renderCompat(guiGraphics: net.minecraft.client.gui.GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
    this.extractRenderState(guiGraphics, mouseX, mouseY, partialTick)
}
//?} else {
fun net.minecraft.client.gui.components.AbstractWidget.renderCompat(guiGraphics: FAGuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
    this.render(guiGraphics, mouseX, mouseY, partialTick)
}
//?}

//? if >=26.2 {
fun net.minecraft.client.gui.GuiGraphicsExtractor.drawString(font: net.minecraft.client.gui.Font, text: net.minecraft.network.chat.Component, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    this.text(font, text, x, y, color, shadow)
}

fun net.minecraft.client.gui.GuiGraphicsExtractor.drawString(font: net.minecraft.client.gui.Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean = false) {
    this.text(font, text, x, y, color, shadow)
}
//?}

fun FAGuiGraphics.pushPose() {
//? if <26.2 {
    this.pose().pushPose()
//?} else {
    this.pose().pushMatrix()
//?}
}

fun FAGuiGraphics.popPose() {
//? if <26.2 {
    this.pose().popPose()
//?} else {
    this.pose().popMatrix()
//?}
}
