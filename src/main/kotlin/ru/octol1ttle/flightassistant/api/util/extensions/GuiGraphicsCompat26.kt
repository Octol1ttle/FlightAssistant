package ru.octol1ttle.flightassistant.api.util.extensions

// Compatibility shims for Minecraft 26.x, where `GuiGraphics` was replaced by the
// `GuiGraphicsExtractor` render-state model. These only apply on 26+, on older
// versions `GuiGraphics` is still a real class with these members.
//? if >=26 {
/*import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import org.joml.Matrix3x2fStack
import org.joml.Quaternionfc
import kotlin.math.atan2

fun GuiGraphicsExtractor.drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean = false): Int {
    text(font, text, x, y, color, shadow)
    return 1
}

fun GuiGraphicsExtractor.drawString(font: Font, text: Component, x: Int, y: Int, color: Int, shadow: Boolean = false): Int {
    text(font, text, x, y, color, shadow)
    return 1
}

fun GuiGraphicsExtractor.drawCenteredString(font: Font, text: Component, x: Int, y: Int, color: Int): Int {
    centeredText(font, text, x, y, color)
    return 1
}

fun GuiGraphicsExtractor.hLine(x1: Int, x2: Int, y: Int, color: Int) {
    horizontalLine(x1, x2, y, color)
}

fun GuiGraphicsExtractor.vLine(x: Int, y1: Int, y2: Int, color: Int) {
    verticalLine(x, y1, y2, color)
}

fun Matrix3x2fStack.translate(x: Float, y: Float, z: Float): Matrix3x2fStack {
    translate(x, y)
    return this
}

fun Matrix3x2fStack.mulPose(q: Quaternionfc): Matrix3x2fStack {
    rotate(2f * atan2(q.z(), q.w()))
    return this
}

fun Matrix3x2fStack.rotateAround(q: Quaternionfc, x: Float, y: Float, z: Float): Matrix3x2fStack {
    translate(x, y)
    rotate(2f * atan2(q.z(), q.w()))
    translate(-x, -y)
    return this
}

// ChatFormatting lost its `color` field in 26.x; provide it as an extension.
val net.minecraft.ChatFormatting.color: Int?
    get() = when (this) {
        net.minecraft.ChatFormatting.BLACK -> 0x000000
        net.minecraft.ChatFormatting.DARK_BLUE -> 0x0000AA
        net.minecraft.ChatFormatting.DARK_GREEN -> 0x00AA00
        net.minecraft.ChatFormatting.DARK_AQUA -> 0x00AAAA
        net.minecraft.ChatFormatting.DARK_RED -> 0xAA0000
        net.minecraft.ChatFormatting.DARK_PURPLE -> 0xAA00AA
        net.minecraft.ChatFormatting.GOLD -> 0xFFAA00
        net.minecraft.ChatFormatting.GRAY -> 0xAAAAAA
        net.minecraft.ChatFormatting.DARK_GRAY -> 0x555555
        net.minecraft.ChatFormatting.BLUE -> 0x5555FF
        net.minecraft.ChatFormatting.GREEN -> 0x55FF55
        net.minecraft.ChatFormatting.AQUA -> 0x55FFFF
        net.minecraft.ChatFormatting.RED -> 0xFF5555
        net.minecraft.ChatFormatting.LIGHT_PURPLE -> 0xFF55FF
        net.minecraft.ChatFormatting.YELLOW -> 0xFFFF55
        net.minecraft.ChatFormatting.WHITE -> 0xFFFFFF
        else -> null
    }

// Minecraft.setScreen was renamed to setScreenAndShow in 26.x, and the current
// screen/overlay moved to Minecraft.gui.
fun net.minecraft.client.Minecraft.setScreen(screen: net.minecraft.client.gui.screens.Screen?) {
    gui.setScreen(screen)
}

val net.minecraft.client.Minecraft.screen: net.minecraft.client.gui.screens.Screen?
    get() = gui.screen()

val net.minecraft.client.Minecraft.overlay: net.minecraft.client.gui.screens.Overlay?
    get() = gui.overlay()

// Renderable no longer has `render`, it now uses `extractRenderState`.
fun net.minecraft.client.gui.components.Renderable.render(guiGraphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
    extractRenderState(guiGraphics, mouseX, mouseY, partialTick)
}
*///?}
