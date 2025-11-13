package ru.octol1ttle.flightassistant.screen.components

import dev.isxander.yacl3.api.NameableEnum
import java.util.function.Consumer
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.api.util.extensions.appendWithSeparation
import ru.octol1ttle.flightassistant.api.util.extensions.font
import ru.octol1ttle.flightassistant.api.util.extensions.whiteColor

class CycleTextOnlyButton<E : NameableEnum>(x: Int, y: Int, private val entries: List<E>, var selected: E, private val onValueChange: Consumer<E>) : AbstractButton(x, y, 0, font.lineHeight, Component.empty()) {
    private var index: Int

    init {
        assert(entries.isNotEmpty()) { "Expected a list of enum entries, got an empty list" }
        index = entries.indexOf(selected)
        assert(index > -1) { "Selected item not present in entries list" }
        refreshMessage()
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val message: Component = TextOnlyButton.getMessageComponent(this)

        this.width = font.width(message)
        guiGraphics.drawString(font, message, this.x, this.y, whiteColor)
    }

//? if >=1.21.9 {
    /*override fun onPress(input: net.minecraft.client.input.InputWithModifiers) {
        if (input.hasShiftDown()) {
            this.cycleValue(-1)
        } else {
            this.cycleValue(1)
        }
    }
*///?} else {
    override fun onPress() {
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            this.cycleValue(-1)
        } else {
            this.cycleValue(1)
        }
    }
//?}

    private fun cycleValue(delta: Int) {
        this.index = Mth.positiveModulo(this.index + delta, this.entries.size)
        selected = this.entries[this.index]
        refreshMessage()
        this.onValueChange.accept(selected)
    }

    private fun refreshMessage() {
        val message: MutableComponent = Component.empty()
        for (i in 0..<this.entries.size) {
            val entry: E = this.entries[i]
            val entryName: Component = if (i == this.index) entry.displayName.copy().withStyle(ACTIVE_STYLE) else entry.displayName.copy().withStyle(INACTIVE_STYLE)
            message.appendWithSeparation(entryName, SEPARATOR)
        }
        this.message = message
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        return this.defaultButtonNarrationText(narrationElementOutput)
    }

    companion object {
        val INACTIVE_STYLE: Style = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)
        val ACTIVE_STYLE: Style = Style.EMPTY.withColor(ChatFormatting.GREEN)
        val SEPARATOR: Component = Component.literal("/").withStyle(INACTIVE_STYLE)
    }
}