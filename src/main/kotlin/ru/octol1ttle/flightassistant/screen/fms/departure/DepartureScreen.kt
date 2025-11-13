package ru.octol1ttle.flightassistant.screen.fms.departure

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.toIntOrNullWithFallback
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox

class DepartureScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms.departure")) {
    private lateinit var discardChanges: Button

    override fun init() {
        super.init()

        val baseX: Int = this.width / 3
        val baseY: Int = this.height / 3
        val baseWidth = 36
        val baseHeight = 12

        val coordinates: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY, Component.translatable("menu.flightassistant.fms.coordinates")))
        val xEditBox: TypeStrictEditBox<Int> = this.addRenderableWidget(TypeStrictEditBox(coordinates.x + coordinates.width, coordinates.y - 2, baseWidth + 4, baseHeight, state.coordinatesX, { state.coordinatesX = it }, String::toIntOrNullWithFallback))
        this.addRenderableWidget(TypeStrictEditBox(xEditBox.x + xEditBox.width + 4, xEditBox.y, xEditBox.width, xEditBox.height, state.coordinatesZ, { state.coordinatesZ = it }, String::toIntOrNullWithFallback))

        val elevation: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 16, Component.translatable("menu.flightassistant.fms.elevation")))
        this.addRenderableWidget(TypeStrictEditBox(elevation.x + elevation.width, elevation.y - 2, baseWidth, baseHeight, state.elevation, { state.elevation = it }, String::toIntOrNullWithFallback))

        val takeoffThrust: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 48, Component.translatable("menu.flightassistant.fms.departure.takeoff_thrust")))
        this.addRenderableWidget(TypeStrictEditBox(takeoffThrust.x + takeoffThrust.width, takeoffThrust.y - 2, baseWidth, baseHeight, state.takeoffThrustPercent, { state.takeoffThrustPercent = it }, String::toIntOrNullWithFallback, { it in 0..100 }))

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = DepartureScreenState.load(computers.plan.departureData)
            this.rebuildWidgets()
        }.pos(this.width - 200, this.height - 30).width(100).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun onClose() {
        state.save(computers.plan)
        super.onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        discardChanges.active = state != DepartureScreenState.load(computers.plan.departureData)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    companion object {
        private var state: DepartureScreenState = DepartureScreenState()

        fun reload(data: FlightPlanComputer.DepartureData) {
            state = DepartureScreenState.load(data)
        }
    }
}