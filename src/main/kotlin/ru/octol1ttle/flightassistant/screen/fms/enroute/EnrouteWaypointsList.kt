package ru.octol1ttle.flightassistant.screen.fms.enroute

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.font
import ru.octol1ttle.flightassistant.api.util.extensions.primaryAdvisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.swap
import ru.octol1ttle.flightassistant.api.util.extensions.toIntOrNullWithFallback
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.components.FABaseList
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox

class EnrouteWaypointsList(y0: Int, y1: Int, width: Int, val columns: Float, val state: EnrouteScreenState) : FABaseList<EnrouteWaypointsList.Entry>(y0, y1, width, ITEM_HEIGHT) {
    init {
        rebuildEntries()
    }

    class Entry(val width: Int, val columns: Float, val state: EnrouteScreenState.Waypoint, val list: EnrouteWaypointsList) : ContainerObjectSelectionList.Entry<Entry>() {
        private val columnWidth: Float = width / this.columns

        private val xEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.coordinatesX, { state.coordinatesX = it }, String::toIntOrNullWithFallback)
        private val zEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.coordinatesZ, { state.coordinatesZ = it }, String::toIntOrNullWithFallback)
        private val altitudeEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.altitude, { state.altitude = it }, String::toIntOrNullWithFallback)
        private val speedEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.speed, { state.speed = it }, String::toIntOrNullWithFallback) { it >= 0 }

        private val directToButton = Button.builder(Component.literal(DIRECT_TO_SYMBOL)) {
            list.state.waypoints.forEach { it.active = null }
            this.state.active = FlightPlanComputer.EnrouteWaypoint.Active.TARGET
        }.size(12, 12).build()
        private val moveUpButton = Button.builder(Component.literal("↑")) {
            list.state.waypoints.swap(index, index - 1)
            list.rebuildEntries()
        }.size(12, 12).build()
        private val moveDownButton = Button.builder(Component.literal("↓")) {
            list.state.waypoints.swap(index, index + 1)
            list.rebuildEntries()
        }.size(12, 12).build()
        private val deleteButton = Button.builder(Component.literal("X")) {
            list.state.waypoints.remove(this.state)
            list.rebuildEntries()
        }.size(12, 12).build()

        private var index: Int = 0
        private var hovering: Boolean = false
        val children = listOf(xEditBox, zEditBox, altitudeEditBox, speedEditBox)
        val childrenWhenHovering = listOf(xEditBox, zEditBox, altitudeEditBox, speedEditBox, directToButton, moveUpButton, moveDownButton, deleteButton)

        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            this.index = index
            this.hovering = hovering
            this.directToButton.active = getActiveSymbol() != DIRECT_TO_SYMBOL
            @Suppress("UsePropertyAccessSyntax")
            this.directToButton.setTooltip(Tooltip.create(DIRECT_TO_TOOLTIP_TEXT))
            if (index == 0) {
                this.moveUpButton.active = false
            }
            if (index == list.children().size - 1) {
                this.moveDownButton.active = false
            }

            val indexX: Int = (width * (0.4f / this.columns)).toInt()
            guiGraphics.drawString((index + 1).toString(), indexX, top, ChatFormatting.WHITE.color!!, true)
            val activeSymbol: String? = getActiveSymbol()
            if (activeSymbol != null) {
                guiGraphics.drawString(activeSymbol, indexX - 15, top, primaryAdvisoryColor, true)
            }

            children().filterIsInstance<TypeStrictEditBox<*>>().forEachIndexed { i, editBox ->
                @Suppress("UsePropertyAccessSyntax")
                editBox.setBordered(false)
                editBox.x = (columnWidth * (i + 1)).toInt()
                editBox.y = top
                editBox.render(guiGraphics, mouseX, mouseY, partialTick)
            }

            var buttonX: Int = (columnWidth * (if (columns <= 7) columns - 1.25f else 6.75f)).toInt()
            children().filterIsInstance<Button>().forEach { button ->
                button.x = buttonX
                buttonX += button.width + 3
                button.y = top
                button.render(guiGraphics, mouseX, mouseY, partialTick)
            }
        }

        private fun getActiveSymbol(): String? {
            val active: FlightPlanComputer.EnrouteWaypoint.Active = state.active ?: return null
            return when (active) {
                FlightPlanComputer.EnrouteWaypoint.Active.ORIGIN -> "→"
                FlightPlanComputer.EnrouteWaypoint.Active.TARGET -> {
                    if (list.state.waypoints.any { it.active == FlightPlanComputer.EnrouteWaypoint.Active.ORIGIN }) "▶"
                    else DIRECT_TO_SYMBOL
                }
            }
        }

        override fun children(): List<GuiEventListener> {
            return if (this.hovering) childrenWhenHovering else children
        }

        override fun narratables(): List<NarratableEntry> {
            return if (this.hovering) childrenWhenHovering else children
        }
    }

    fun rebuildEntries() {
        this.clearEntries()
        for (waypoint: EnrouteScreenState.Waypoint in state.waypoints) {
            addEntry(Entry(this.width, this.columns, waypoint, this))
        }
    }

    companion object {
        private const val ITEM_HEIGHT: Int = 12
        private const val DIRECT_TO_SYMBOL: String = "⏭"
        private val DIRECT_TO_TOOLTIP_TEXT: Component = Component.translatable("menu.flightassistant.fms.enroute.direct_to")
    }
}