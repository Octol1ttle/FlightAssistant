package ru.octol1ttle.flightassistant.screen

import kotlin.properties.Delegates
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost

abstract class FABaseScreen(val parent: Screen?, title: Component) : Screen(title) {
    protected val computers: ComputerBus = ComputerHost
    protected var centerX by Delegates.notNull<Int>()
    protected var centerY by Delegates.notNull<Int>()

    override fun init() {
        this.centerX = this.width / 2
        this.centerY = this.height / 2
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
//? if <1.21.6 {
        this.renderBackground(
            guiGraphics
            /*? if >=1.21 {*//*, mouseX, mouseY, delta *///?}
        )
//?}
        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun onClose() {
        this.minecraft!!.setScreen(parent)
    }

    override fun isPauseScreen(): Boolean = false
}
