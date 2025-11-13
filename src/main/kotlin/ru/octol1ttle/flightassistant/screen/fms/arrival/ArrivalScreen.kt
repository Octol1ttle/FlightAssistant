package ru.octol1ttle.flightassistant.screen.fms.arrival

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.toIntOrNullWithFallback
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.CycleTextOnlyButton
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox

class ArrivalScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms.arrival")) {
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

        val landingThrust: SmartStringWidget = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 48, Component.translatable("menu.flightassistant.fms.arrival.landing_thrust")))
        this.addRenderableWidget(TypeStrictEditBox(landingThrust.x + landingThrust.width, landingThrust.y - 2, baseWidth, baseHeight, state.landingThrustPercent, { state.landingThrustPercent = it }, String::toIntOrNullWithFallback, { it in 0..100 }))

        val minimums = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 64, Component.translatable("menu.flightassistant.fms.arrival.minimums")))
        val minimumsEditBox = this.addRenderableWidget(TypeStrictEditBox(minimums.x + minimums.width, minimums.y - 2, baseWidth, baseHeight, state.minimums, { state.minimums = it }, String::toIntOrNullWithFallback,
            { if (state.minimumsType == FlightPlanComputer.ArrivalData.MinimumsType.RELATIVE) it >= 0 else true }))
        this.addRenderableWidget(CycleTextOnlyButton(minimumsEditBox.x + minimumsEditBox.width + 5, minimumsEditBox.y + 2, FlightPlanComputer.ArrivalData.MinimumsType.entries, state.minimumsType) { state.minimumsType = it; })

        val goAroundAltitude = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 80, Component.translatable("menu.flightassistant.fms.arrival.go_around_altitude")))
        this.addRenderableWidget(TypeStrictEditBox(goAroundAltitude.x + goAroundAltitude.width, goAroundAltitude.y - 2, baseWidth + 4, baseHeight, state.goAroundAltitude, { state.goAroundAltitude = it }, String::toIntOrNullWithFallback))

        val approachReEntryWaypointIndex = this.addRenderableWidget(SmartStringWidget(baseX, baseY + 96, Component.translatable("menu.flightassistant.fms.arrival.approach_re_entry_waypoint_index")))
        this.addRenderableWidget(TypeStrictEditBox(approachReEntryWaypointIndex.x + approachReEntryWaypointIndex.width, approachReEntryWaypointIndex.y - 2, baseWidth + 4, baseHeight, state.approachReEntryWaypointIndex, { state.approachReEntryWaypointIndex = it }, String::toIntOrNullWithFallback,
            { it >= 0 }))

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = ArrivalScreenState.load(computers.plan.arrivalData)
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
        discardChanges.active = state != ArrivalScreenState.load(computers.plan.arrivalData)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    companion object {
        private var state = ArrivalScreenState()

        fun reload(data: FlightPlanComputer.ArrivalData) {
            state = ArrivalScreenState.load(data)
        }
    }
}