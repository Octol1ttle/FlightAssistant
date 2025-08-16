package ru.octol1ttle.flightassistant.screen

import dev.architectury.platform.Platform
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.fms.departure.DepartureScreen
import ru.octol1ttle.flightassistant.screen.fms.enroute.EnrouteScreen
import ru.octol1ttle.flightassistant.screen.system.SystemManagementScreen

class FlightAssistantSetupScreen : FABaseScreen(null, Component.translatable("menu.flightassistant")) {
    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 15, this.width, this.font.lineHeight, this.title, this.font))

        this.addRenderableWidget(StringWidget(0, this.centerY - 80, this.width, this.font.lineHeight, Component.translatable("menu.flightassistant.system"), this.font))
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_displays")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_displays"), "menu.flightassistant.system.name.hud", HudDisplayHost)
            )
        }.pos(this.centerX - 105, this.centerY - 65).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_computers")) {
            this.minecraft!!.setScreen(
                SystemManagementScreen(
                    this,
                Component.translatable("menu.flightassistant.system.manage_computers"), "menu.flightassistant.system.name.computer", ComputerHost)
            )
        }.pos(this.centerX + 5, this.centerY - 65).width(100).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreen(AutoFlightScreen(this))
        }.pos(this.centerX - 80, this.centerY - 30).width(160).build())

        this.addRenderableWidget(StringWidget(0, this.centerY + 5, this.width, this.font.lineHeight, Component.translatable("menu.flightassistant.fms"), this.font))
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.departure")) {
            this.minecraft!!.setScreen(DepartureScreen(this))
        }.pos(this.centerX - 130, this.centerY + 20).width(80).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute")) {
            this.minecraft!!.setScreen(EnrouteScreen(this))
        }.pos(this.centerX - 40, this.centerY + 20).width(80).build()).active = Platform.isDevelopmentEnvironment()
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.arrival")) {
            //this.minecraft!!.setScreen(FlightPlanScreen())
        }.pos(this.centerX + 50, this.centerY + 20).width(80).build()).active = Platform.isDevelopmentEnvironment()

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreen(FAConfigScreen.generate(this))
        }.pos(10, this.height - 30).width(120).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }
}
