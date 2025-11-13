package ru.octol1ttle.flightassistant.screen.system

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemManagementScreen(parent: Screen, title: Component, private val baseKey: String, private val controller: ModuleController<*>) : FABaseScreen(parent, title) {
    override fun init() {
        super.init()

        val top = 20
        val bottom: Int = this.height - 40
        this.addRenderableWidget(SystemManagementList(top, bottom, this.width, baseKey, controller))

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }
}
