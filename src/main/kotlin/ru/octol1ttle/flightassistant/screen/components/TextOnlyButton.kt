package ru.octol1ttle.flightassistant.screen.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TextOnlyButton(val baseX: Int, y: Int, text: Component, val centered: Boolean = true, onPress: OnPress) : Button(baseX, y, font.width(text), font.lineHeight, text, onPress, DEFAULT_NARRATION) {
    var color: Int = 0

//? if >=1.21.11 {
    /*override fun renderContents(
*///?} else
    override fun renderWidget(
        guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val message: Component = getMessageComponent(this)

        this.width = font.width(message)
        this.x =
            if (centered) this.baseX - this.width / 2
            else this.baseX
        guiGraphics.drawString(font, message, this.x, this.y, this.color or (Mth.ceil(this.alpha * 255.0f) shl 24))
    }

    companion object {
        private val UNDERLINED: Style = Style.EMPTY.withUnderlined(true)

        fun getMessageComponent(widget: AbstractWidget): Component {
            return if (widget.isHoveredOrFocused) {
                ComponentUtils.mergeStyles(widget.message.copy(), UNDERLINED)
            } else {
                widget.message
            }
        }
    }
}