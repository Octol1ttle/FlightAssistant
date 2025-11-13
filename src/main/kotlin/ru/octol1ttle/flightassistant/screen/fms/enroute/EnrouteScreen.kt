package ru.octol1ttle.flightassistant.screen.fms.enroute

import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.chat.Component.translatable
import net.minecraft.network.chat.Style
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.setColor
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget

class EnrouteScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms.enroute")) {
    private lateinit var deleteAll: Button
    private lateinit var discardChanges: Button
    private lateinit var save: Button
    private lateinit var done: Button

    override fun init() {
        super.init()

        val columnsSizeWithMargin: Float = COLUMNS.size + HOVERING_COLUMNS_MARGIN
        val optimumColumnsSize: Float = (if (this.width / columnsSizeWithMargin >= 75) columnsSizeWithMargin else COLUMNS.size.toFloat())
        COLUMNS.forEachIndexed { i, component ->
            this.addRenderableWidget(SmartStringWidget((this.width * (max(0.4f, i.toFloat()) / optimumColumnsSize)).toInt(), Y0, component).setColor(ChatFormatting.GRAY.color!!))
        }

        val list: EnrouteWaypointsList = this.addRenderableWidget(EnrouteWaypointsList(Y0 + 10, this.height - Y0 * 2, this.width, optimumColumnsSize, computers, state))

        deleteAll = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute.delete_all")) {
            state.waypoints.clear()
            list.rebuildEntries()
        }.bounds(10, this.height - Y0 * 2 + 5, 80, 20).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute.add_waypoint")) {
            state.waypoints.add(EnrouteScreenState.Waypoint(active = if (state.waypoints.isEmpty()) FlightPlanComputer.EnrouteWaypoint.Active.TARGET else null))
            list.rebuildEntries()
        }.bounds(this.centerX - 50, this.height - Y0 * 2 + 5, 100, 20).build())

        @Suppress("UsePropertyAccessSyntax")
        this.addRenderableWidget(SmartStringWidget(10, this.height - 24,
            Component.translatable("menu.flightassistant.fms.enroute.legend.hover").setStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY.color!!))
        )).setTooltip(Tooltip.create(LEGEND_TOOLTIP_TEXT))

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = EnrouteScreenState.load(computers.plan.enrouteData)
            this.rebuildWidgets()
        }.pos(this.width - 290, this.height - 30).width(100).build())

        save = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.save")) { _: Button? ->
            state.save(computers.plan)
            list.rebuildEntries()
        }.pos(this.width - 180, this.height - 30).width(80).build())

        done = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        deleteAll.active = state.waypoints.isNotEmpty()

        val hasUnsavedChanges: Boolean = !state.equals(EnrouteScreenState.load(computers.plan.enrouteData))
        save.active = hasUnsavedChanges
        discardChanges.active = hasUnsavedChanges
        done.active = !hasUnsavedChanges

        super.render(guiGraphics, mouseX, mouseY, delta)

        if (hasUnsavedChanges) {
            val text: Component = Component.translatable("menu.flightassistant.fms.enroute.unsaved_changes")
            guiGraphics.drawMiddleAlignedString(text, this.width / 4, 7, ChatFormatting.YELLOW.color!!, true)
        }
    }

    companion object {
        private const val Y0: Int = 30
        private const val HOVERING_COLUMNS_MARGIN: Float = 0.75f
        private val COLUMNS: Array<Component> = arrayOf(literal("#"), literal("X"), literal("Z"), translatable("short.flightassistant.altitude"), translatable("short.flightassistant.speed"), translatable("short.flightassistant.distance"), translatable("short.flightassistant.time"))
        private val LEGEND_TOOLTIP_TEXT: Component = translatable("%s\n%s\n%s",
            translatable("menu.flightassistant.fms.enroute.legend.origin", literal(EnrouteWaypointsList.FROM_SYMBOL).setColor(primaryAdvisoryColor)),
            translatable("menu.flightassistant.fms.enroute.legend.target", literal(EnrouteWaypointsList.TO_SYMBOL).setColor(primaryAdvisoryColor)),
            translatable("menu.flightassistant.fms.enroute.legend.direct-to", literal(EnrouteWaypointsList.DIRECT_TO_SYMBOL).setColor(primaryAdvisoryColor))
        )

        private var state: EnrouteScreenState = EnrouteScreenState()

        fun reload(waypoints: List<FlightPlanComputer.EnrouteWaypoint>) {
            state = EnrouteScreenState.load(waypoints)
        }
    }
}