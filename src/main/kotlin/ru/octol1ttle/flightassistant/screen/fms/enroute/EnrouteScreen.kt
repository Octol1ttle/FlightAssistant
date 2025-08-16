package ru.octol1ttle.flightassistant.screen.fms.enroute

import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.chat.Component.translatable
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget

class EnrouteScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms.enroute")) {
    private lateinit var discardChanges: Button
    private lateinit var save: Button
    private lateinit var done: Button

    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        val columnsSizeWithMargin: Float = COLUMNS.size + HOVERING_COLUMNS_MARGIN
        val optimumColumnsSize: Float = (if (this.width / columnsSizeWithMargin >= 75) columnsSizeWithMargin else COLUMNS.size.toFloat())
        COLUMNS.forEachIndexed { i, component ->
            this.addRenderableWidget(SmartStringWidget((this.width * (max(0.4f, i.toFloat()) / optimumColumnsSize)).toInt(), Y0, component).setColor(ChatFormatting.GRAY.color!!))
        }

        val list: EnrouteWaypointsList = this.addRenderableWidget(EnrouteWaypointsList(Y0 + 10, this.height - Y0 * 2, this.width, optimumColumnsSize, state))

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute.add_waypoint")) {
            state.waypoints.add(EnrouteScreenState.Waypoint(active = if (state.waypoints.isEmpty()) FlightPlanComputer.EnrouteWaypoint.Active.TARGET else null))
            list.rebuildEntries()
        }.bounds(this.centerX - 50, this.height - Y0 * 2 + 5, 100, 20).build())

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = lastState.copy()
            this.rebuildWidgets()
        }.pos(this.width - 290, this.height - 30).width(100).build())

        save = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.save")) { _: Button? ->
            lastState = state.copy()
            state.save(computers.plan)
            list.rebuildEntries()
        }.pos(this.width - 180, this.height - 30).width(80).build())

        done = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        save.active = !state.equals(lastState)
        discardChanges.active = save.active
        done.active = !save.active

        super.render(guiGraphics, mouseX, mouseY, delta)

        if (save.active) {
            val text: Component = Component.translatable("menu.flightassistant.fms.enroute.unsaved_changes")
            guiGraphics.drawMiddleAlignedString(text, this.width / 4, 7, ChatFormatting.YELLOW.color!!, true)
        }
    }

    companion object {
        private const val Y0: Int = 30

        private val COLUMNS: Array<Component> = arrayOf(literal("#"), literal("X"), literal("Z"), translatable("short.flightassistant.altitude"), translatable("short.flightassistant.speed"), translatable("short.flightassistant.distance"), translatable("short.flightassistant.time"))
        private const val HOVERING_COLUMNS_MARGIN: Float = 0.75f

        private var lastState: EnrouteScreenState = EnrouteScreenState()
        private var state: EnrouteScreenState = EnrouteScreenState()
    }
}