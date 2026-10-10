package ru.octol1ttle.flightassistant.screen.waypoints

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer.Waypoint.PhaseMarker
import ru.octol1ttle.flightassistant.screen.components.FABaseList
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox
import kotlin.math.roundToInt

class WaypointsList(y0: Int, y1: Int, width: Int, val computers: ComputerBus, val state: WaypointsScreenState) : FABaseList<WaypointsList.Entry>(y0, y1, width, ITEM_HEIGHT) {
    init {
        rebuildEntries()
    }

    abstract class Entry(@JvmField val width: Int) : ContainerObjectSelectionList.Entry<Entry>()

    class PhaseEntry(width: Int, val phase: Component) : Entry(width) {
        //? if >=1.21.9 {
        /*override fun renderContent(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            val left = contentX
            val top = contentY
            val index = list.children().indexOf(this)
*///?} else {
        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
//?}
            val x = left + 30
            val y = top + height / 2
            guiGraphics.drawString(phase, x, y - 3, grayColor, true)
            guiGraphics.hLine(x + textWidth(phase) + 5, width - 40, y, grayColor)
        }

        override fun children(): List<GuiEventListener?> {
            return emptyList()
        }

        override fun narratables(): List<NarratableEntry?> {
            return emptyList()
        }
    }

    class WaypointEntry(width: Int, val computers: ComputerBus, val list: WaypointsList, val state: WaypointsScreenState.Waypoint) : Entry(width) {
        private val coordWidth = mc.font.width("-99999999") + 5
        private val otherWidth = mc.font.width("99999") + 5

        private val xEditBox = TypeStrictEditBox(0, 0, coordWidth, font.lineHeight,
            state.posX, { state.posX = it },
            String::toIntOrNullWithFallback, TypeStrictEditBox.XZ_FILTER)
        private val zEditBox = TypeStrictEditBox(0, 0, coordWidth, font.lineHeight,
            state.posZ, { state.posZ = it },
            String::toIntOrNullWithFallback, TypeStrictEditBox.XZ_FILTER)
        private val altitudeEditBox = TypeStrictEditBox(0, 0, otherWidth, font.lineHeight,
            state.altitude, { state.altitude = it },
            String::toIntOrNullWithFallback) { it in -1000..10000 }
        private val speedEditBox = TypeStrictEditBox(0, 0, otherWidth, font.lineHeight, state.speed, { state.speed = it },
            String::toIntOrNullWithFallback) { it >= 0 }

        private val setTakeoffButton = Button.builder(Component.translatable("menu.flightassistant.fms.phase_marker.takeoff")) {
            state.phaseMarker = PhaseMarker.TAKEOFF
            list.rebuildEntries()
        }.tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.phase_marker.takeoff.tooltip"))).size(22, 14).build()
        private val setApproachButton = Button.builder(Component.translatable("menu.flightassistant.fms.phase_marker.approach")) {
            state.phaseMarker = PhaseMarker.APPROACH
            list.rebuildEntries()
        }.tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.phase_marker.approach.tooltip"))).size(28, 14).build()
        private val setLandButton = Button.builder(Component.translatable("menu.flightassistant.fms.phase_marker.land")) {
            state.phaseMarker = PhaseMarker.LANDING
            list.rebuildEntries()
        }.tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.phase_marker.land.tooltip"))).size(28, 14).build()

        private val directToButton = Button.builder(Component.literal("D→")) {
            list.state.tracking = FlightPlanComputer.ActiveSegment.Direct(this.index)
            list.state.syncedWithComputer = false
        }.tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.direct_to"))).size(20, 14).build()
        private val deleteButton = Button.builder(Component.literal("-")) {
            list.state.waypoints.remove(this.state)
            list.rebuildEntries()
        }.tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.delete"))).size(14, 14).build()

        private val editBoxes = listOf(xEditBox, zEditBox, altitudeEditBox, speedEditBox)
        private val mainButtons = listOf(directToButton, deleteButton)

        var index: Int = 0

        private var hovering: Boolean = false
        private var holdStartTime: Int = 0

        private fun isOrigin(): Boolean {
            val tracking = list.state.tracking
            return tracking is FlightPlanComputer.ActiveSegment.Leg && tracking.origin == state.toFlightPlanWaypoint()
        }

        override fun isDragging(): Boolean {
            return super.isDragging() && !isOrigin()
        }

//? if >=1.21.9 {
        /*override fun renderContent(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            val left = contentX
            val top = contentY
            val index = list.children().indexOf(this)
*///?} else {
        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
//?}
            this.index = list.fromListIndex(index)
            this.hovering = hovering

            val x = left + 35
            val y = top + 2
            val y1 = y + 10

            if (WaypointsScreen.problems.any { it.waypoint == state }) {
                guiGraphics.fill(x - 7, top + 2, x - 5, top + height - 2, ChatFormatting.RED.color!! or (255 shl 24))
            }

            val anyDragging = list.children().any(Entry::isDragging)
            val showDragVisual = this.isDragging && FATickCounter.totalTicks - holdStartTime >= 4
            if (this.isDragging) {
                if (showDragVisual || !hovering) {
                    guiGraphics.renderOutline(x - 3, top, width - 70, height, whiteColor)
                }
            } else {
                if (anyDragging && hovering) {
                    guiGraphics.renderOutline(x - 3, top - 3, width - 70, 1, grayColor)
                }
            }

            guiGraphics.drawString("X", x, y, grayColor, true)
            xEditBox.x = x + 10
            xEditBox.y = y

            guiGraphics.drawString("Z", x, y1, grayColor, true)
            zEditBox.x = x + 10
            zEditBox.y = y1

            val x1 = xEditBox.x + xEditBox.width + 5
            val coordYText = Component.translatable(
                if (state.phaseMarker == PhaseMarker.TAKEOFF || state.phaseMarker == PhaseMarker.LANDING) "short.flightassistant.elevation"
                else "short.flightassistant.altitude"
            )
            guiGraphics.drawString(coordYText, x1, y, grayColor, true)
            altitudeEditBox.x = x1 + 30
            altitudeEditBox.y = y

            guiGraphics.drawString(Component.translatable("short.flightassistant.speed"), x1, y1, grayColor, true)
            speedEditBox.x = x1 + 30
            speedEditBox.y = y1

            val tracking = list.state.tracking
            val targetIndex = when (tracking) {
                is FlightPlanComputer.ActiveSegment.Direct -> tracking.index
                is FlightPlanComputer.ActiveSegment.Leg -> tracking.targetIndex
                null -> -1
            }
            val mode = distanceTimeMode.resolve(computers.data.flying)
            if (targetIndex > -1 && this.index >= targetIndex && (this.index > 0 || mode == DistanceTimeMode.REMAINING)) {
                val target = list.state.waypoints[targetIndex]
                val dist = when (mode) {
                    DistanceTimeMode.REMAINING -> {
                        distance2d(target.posX, target.posZ, computers.data.position.x, computers.data.position.z) +
                            list.state.waypoints.subList(targetIndex, this.index + 1).zipWithNext().sumOf { (prev, next) ->
                                distance2d(prev.posX, prev.posZ, next.posX, next.posZ)
                            }
                    }
                    DistanceTimeMode.BETWEEN_WAYPOINTS -> {
                        val last = list.state.waypoints[this.index - 1]
                        distance2d(state.posX, state.posZ, last.posX, last.posZ)
                    }
                    else -> throw AssertionError()
                }

                val x2 = altitudeEditBox.x + altitudeEditBox.width + 5
                guiGraphics.drawString(Component.translatable("short.flightassistant.distance"), x2, y, grayColor, true)
                guiGraphics.drawString(dist.roundToInt().toString(), x2 + 30, y, whiteColor, true)
                guiGraphics.drawString(Component.translatable("short.flightassistant.time"), x2, y1, grayColor, true)
                guiGraphics.drawString(computers.plan.getFormattedTime(dist), x2 + 30, y1, whiteColor, true)
            }

            val right = x + width - 80
            children().filterIsInstance<TypeStrictEditBox<*>>().forEach {
                @Suppress("UsePropertyAccessSyntax")
                it.setBordered(false)
            }

            var buttonRight = right
            children().filterIsInstance<Button>().reversed().forEach { button ->
                buttonRight -= button.width + 5
                button.x = buttonRight
                button.y = y + 2
                button.visible = !anyDragging && !showDragVisual
            }
            setLandButton.active = state.phaseMarker != PhaseMarker.LANDING
            setApproachButton.active = state.phaseMarker != PhaseMarker.APPROACH
            directToButton.active = tracking != FlightPlanComputer.ActiveSegment.Direct(this.index)
            deleteButton.active = !isOrigin()
            children().filterIsInstance<Renderable>().forEach { it.render(guiGraphics, mouseX, mouseY, partialTick) }
        }

        private fun childrenInternal() : List<AbstractWidget> {
            if (!this.hovering) {
                return editBoxes
            }

            if (state.phaseMarker == PhaseMarker.APPROACH) {
                return editBoxes + mainButtons
            }
            if (state.phaseMarker == PhaseMarker.NONE) {
                val hasLand = list.state.waypoints.any { it.phaseMarker == PhaseMarker.LANDING }
                if (this.index == 0) {
                    return editBoxes + (if (hasLand) listOf(setApproachButton, setTakeoffButton) else listOf(setTakeoffButton)) + mainButtons
                }
                if (!hasLand) {
                    return editBoxes + setLandButton + mainButtons
                }
                val landIndex = list.state.waypoints.indexOfFirst { it.phaseMarker == PhaseMarker.LANDING }
                if (!list.state.waypoints.any { it.phaseMarker == PhaseMarker.APPROACH } && this.index < landIndex) {
                    return editBoxes + setApproachButton + mainButtons
                }

                return editBoxes + mainButtons
            }

            return editBoxes + deleteButton
        }

        override fun children(): List<GuiEventListener> = childrenInternal()
        override fun narratables(): List<NarratableEntry> = childrenInternal()

//? if >=1.21.9 {
        /*override fun mouseClicked(event: MouseButtonEvent, isDoubleClick: Boolean): Boolean {
            val button = event.button()
            val callSuper = { super.mouseClicked(event, isDoubleClick) }
*///?} else {
        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            val callSuper = { super.mouseClicked(mouseX, mouseY, button) }
//?}
            if (this.hovering && button == 0) {
                if (!this.isDragging) {
                    holdStartTime = FATickCounter.totalTicks
                }
                this.isDragging = true
                callSuper()
                return true
            }

            return callSuper()
        }

//? if >=1.21.9 {
        /*override fun mouseReleased(event: MouseButtonEvent): Boolean {
            val button = event.button()
            val callSuper = { super.mouseReleased(event) }
*///?} else {
        override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
//?}
            val callSuper = { super.mouseReleased(mouseX, mouseY, button) }
            val entryAtPosition = list.getEntryAtPosition(mouseX, mouseY)
            val waypoints = list.state.waypoints
            if (this.isDragging) {
                if (entryAtPosition is WaypointEntry && entryAtPosition.state != this.state && waypoints.remove(this.state)) {
                    waypoints.add(waypoints.indexOf(entryAtPosition.state), this.state)
                    list.rebuildEntries()
                    callSuper()
                    return true
                }
                if (mouseY > list.endY && waypoints.remove(this.state)) {
                    waypoints.add(this.state)
                    list.rebuildEntries()
                    callSuper()
                    return true
                }
            }

            return callSuper()
        }
    }

    val endY: Int
        get() = y0 + maxPosition + 3

    fun fromListIndex(listIndex: Int): Int {
        var index = listIndex
        for (i in 0..listIndex) {
            if (getEntry(i) is PhaseEntry) {
                index--
            }
        }
        return index
    }

    private fun toListIndex(waypointIndex: Int): Int {
        return children().indexOfFirst { it is WaypointEntry && it.index == waypointIndex }
    }

    override fun renderList(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderList(guiGraphics, mouseX, mouseY, partialTick)
        when (val tracking = state.tracking) {
            is FlightPlanComputer.ActiveSegment.Direct -> {
                val y = getRowTop(toListIndex(tracking.index)) + itemHeight / 2 - 2
                guiGraphics.hLine(rowLeft + 10, rowLeft + 22, y, primaryAdvisoryColor)
                guiGraphics.drawString(">", rowLeft + 22, y - 3, primaryAdvisoryColor)
            }
            is FlightPlanComputer.ActiveSegment.Leg -> {
                val targetY = getRowTop(toListIndex(tracking.targetIndex)) + itemHeight / 2 - 2
                guiGraphics.hLine(rowLeft + 10, rowLeft + 22, targetY, primaryAdvisoryColor)
                guiGraphics.drawString(">", rowLeft + 22, targetY - 3, primaryAdvisoryColor)

                val originIndex = state.waypoints.indexOf(WaypointsScreenState.Waypoint(tracking.origin))
                if (originIndex > -1) {
                    val originY = getRowTop(toListIndex(originIndex)) + itemHeight / 2 - 2
                    guiGraphics.hLine(rowLeft + 10, rowLeft + 25, originY, primaryAdvisoryColor)
                    guiGraphics.vLine(rowLeft + 10, originY, targetY, primaryAdvisoryColor)
                }
            }
            null -> {}
        }
        if (children().any(Entry::isDragging) && mouseY > endY) {
            guiGraphics.renderOutline(this.rowLeft + 32, endY, width - 70, 1, grayColor)
        }
    }

    fun rebuildEntries() {
        this.clearEntries()

        if (state.waypoints.isEmpty()) {
            return
        }

        val cruiseAltitude = state.waypoints.maxOf { it.altitude }
        val cruiseStart = state.waypoints.indexOfFirst { it.altitude == cruiseAltitude }
        val cruiseEnd = state.waypoints.indexOfLast { it.altitude == cruiseAltitude }

        var currentPhase = FlightPlanComputer.FlightPhase.UNKNOWN
        for ((i, waypoint: WaypointsScreenState.Waypoint) in state.waypoints.withIndex()) {
            // Handle explicit phase markers first
            if (waypoint.phaseMarker != PhaseMarker.NONE) {
                when (waypoint.phaseMarker) {
                    PhaseMarker.TAKEOFF -> {
                        addEntry(PhaseEntry(this.width, Component.literal("Takeoff")))
                        currentPhase = FlightPlanComputer.FlightPhase.TAKEOFF
                    }
                    PhaseMarker.APPROACH -> {
                        addEntry(PhaseEntry(this.width, Component.literal("Approach")))
                        currentPhase = FlightPlanComputer.FlightPhase.APPROACH
                    }
                    PhaseMarker.LANDING -> {
                        addEntry(PhaseEntry(this.width, Component.literal("Landing")))
                        currentPhase = FlightPlanComputer.FlightPhase.LANDING
                    }
                    else -> {}
                }
            } else if (currentPhase == FlightPlanComputer.FlightPhase.LANDING) {
                addEntry(PhaseEntry(this.width, Component.literal("Go around")))
                currentPhase = FlightPlanComputer.FlightPhase.GO_AROUND
            } else if (currentPhase == FlightPlanComputer.FlightPhase.GO_AROUND) {
                // Don't switch phase
            } else if (i < cruiseStart && currentPhase != FlightPlanComputer.FlightPhase.CLIMB) {
                addEntry(PhaseEntry(this.width, Component.literal("Climb")))
                currentPhase = FlightPlanComputer.FlightPhase.CLIMB
            } else if (i == cruiseStart && currentPhase != FlightPlanComputer.FlightPhase.CRUISE) {
                addEntry(PhaseEntry(this.width, Component.literal("Cruise")))
                currentPhase = FlightPlanComputer.FlightPhase.CRUISE
            } else if (i > cruiseEnd && currentPhase != FlightPlanComputer.FlightPhase.DESCEND) {
                addEntry(PhaseEntry(this.width, Component.literal("Descend")))
                currentPhase = FlightPlanComputer.FlightPhase.DESCEND
            }

            addEntry(WaypointEntry(this.width, this.computers, this, waypoint))
        }
    }

    enum class DistanceTimeMode(@JvmField val displayName: Component) : NameableEnum {
        AUTO(Component.translatable("menu.flightassistant.fms.distance_time_mode.auto")),
        REMAINING(Component.translatable("menu.flightassistant.fms.distance_time_mode.remaining")),
        BETWEEN_WAYPOINTS(Component.translatable("menu.flightassistant.fms.distance_time_mode.between_waypoints"));

        fun resolve(flying: Boolean): DistanceTimeMode {
            return when (this) {
                AUTO -> if (flying) REMAINING else BETWEEN_WAYPOINTS
                else -> this
            }
        }

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }

    companion object {
        private const val ITEM_HEIGHT = 26

        var distanceTimeMode = DistanceTimeMode.AUTO
    }
}