package ru.octol1ttle.flightassistant.screen.waypoints

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.CycleTextOnlyButton
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget
import kotlin.math.max

class WaypointsScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms")) {
    private lateinit var resetTakeoffLanding: Button
    private lateinit var clear: Button
    private lateinit var undo: Button
    private lateinit var apply: Button
    private lateinit var done: Button

    override fun init() {
        super.init()

        val list: WaypointsList = this.addRenderableWidget(WaypointsList(Y0, this.height - Y0, this.width, computers, state))

        resetTakeoffLanding = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.reset_takeoff_landing")) {
            state.waypoints.forEach { it.phaseMarker = FlightPlanComputer.Waypoint.PhaseMarker.NONE }
            list.rebuildEntries()
        }.bounds(max(this.centerX + 50, this.width * 3 / 4 - 150), 5, 150, 20).build())

        this.addRenderableWidget(Button.builder(Component.literal("+")) {
            state.waypoints.add(WaypointsScreenState.Waypoint())
            if (state.waypoints.size == 1) {
                state.tracking = FlightPlanComputer.ActiveSegment.Direct(0)
                state.syncedWithComputer = false
            }
            list.rebuildEntries()
        }.bounds(this.centerX - 50, this.height - Y0, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.add_waypoint")))
            .build())

        clear = this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDDD1")) {
            state.waypoints.clear()
            state.tracking = null
            list.rebuildEntries()
        }.bounds(this.centerX - 25, this.height - Y0, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.delete_all")))
            .build())

        apply = this.addRenderableWidget(Button.builder(Component.literal("✔")) { _: Button? ->
            state.save(computers.plan)
            state.syncedWithComputer = true
            list.rebuildEntries()
        }.bounds(this.centerX + 5, this.height - Y0, 20, 20)
            .tooltip(applyTooltip)
            .build())

        undo = this.addRenderableWidget(Button.builder(Component.literal("❌")) { _: Button? ->
            state = WaypointsScreenState.load(computers.plan)
            this.rebuildWidgets()
        }.bounds(this.centerX + 30, this.height - Y0, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("menu.flightassistant.fms.undo")))
            .build())

        done = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - Y0).width(80).build())

        val string = this.addRenderableWidget(SmartStringWidget(10, this.height - Y0 + 5, Component.translatable("menu.flightassistant.fms.distance_time_mode")))
        this.addRenderableWidget(CycleTextOnlyButton(string.x + string.width, string.y, WaypointsList.DistanceTimeMode.entries, WaypointsList.distanceTimeMode, true) { WaypointsList.distanceTimeMode = it })
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        clear.active = state.waypoints.isNotEmpty()

        if (state.syncedWithComputer && state.tracking != computers.plan.tracking) {
            state.tracking = computers.plan.tracking
        }

        when (val tracking = state.tracking) {
            is FlightPlanComputer.ActiveSegment.Direct -> {
                if (tracking.index >= state.waypoints.size) {
                    state.tracking = null
                    state.syncedWithComputer = false
                }
            }
            is FlightPlanComputer.ActiveSegment.Leg -> {
                if (tracking.targetIndex >= state.waypoints.size) {
                    state.tracking = null
                    state.syncedWithComputer = false
                }
            }
            null -> {}
        }

        for (waypoint in state.waypoints) {
            if (waypoint.phaseMarker == FlightPlanComputer.Waypoint.PhaseMarker.APPROACH
                && !state.waypoints.any { it.phaseMarker == FlightPlanComputer.Waypoint.PhaseMarker.LANDING }) {
                waypoint.phaseMarker = FlightPlanComputer.Waypoint.PhaseMarker.NONE
                rebuildWidgets()
            }
        }

        detectProblems()

        resetTakeoffLanding.visible = state.waypoints.any { it.phaseMarker != FlightPlanComputer.Waypoint.PhaseMarker.NONE }

        val hasUnsavedChanges: Boolean = !state.equals(WaypointsScreenState.load(computers.plan))
        apply.active = hasUnsavedChanges && problems.isEmpty()
        undo.active = hasUnsavedChanges
        done.active = !hasUnsavedChanges

        super.render(guiGraphics, mouseX, mouseY, delta)

        if (hasUnsavedChanges) {
            val (text, color) = if (problems.isNotEmpty()) Component.translatable("menu.flightassistant.fms.problems") to ChatFormatting.RED.color!!
            else Component.translatable("menu.flightassistant.fms.unsaved_changes") to ChatFormatting.YELLOW.color!!
            guiGraphics.drawMiddleAlignedString(text, this.width / 4, 7, color, true)

            if (done.isHovered) {
                guiGraphics.renderOutline(apply.x - 5, apply.y - 5, 55, 30, warningColor)
            }
        }
    }

    private fun detectProblems() {
        problems.clear()

        if (state.waypoints.size == 1) {
            problems.add(Problem(null, true, Component.translatable("menu.flightassistant.fms.problems.not_enough_waypoints")))
        }

        state.waypoints.zipWithNext().forEach { (waypoint, next) ->
            if (waypoint.posX == next.posX && waypoint.posZ == next.posZ) {
                val text = Component.translatable(
                    "menu.flightassistant.fms.problems.adjacent_with_same_position",
                    waypoint.posX,
                    waypoint.posZ
                )
                problems.add(Problem(waypoint, true, text))
                problems.add(Problem(next, true, text))
            }
        }

        val landing = state.waypoints.singleOrNull { it.phaseMarker == FlightPlanComputer.Waypoint.PhaseMarker.LANDING }
        if (landing != null && !state.waypoints.any { it.phaseMarker == FlightPlanComputer.Waypoint.PhaseMarker.APPROACH}) {
            problems.add(Problem(landing, true, Component.translatable("menu.flightassistant.fms.problems.no_approach_start")))
        }

        @Suppress("UsePropertyAccessSyntax")
        apply.setTooltip(if (problems.isEmpty()) applyTooltip else Tooltip.create(problems.distinctBy { it.text }.map { it.text }.reduce { acc, component ->
            acc.copy().append("\n").append(component)
        }))
    }

    data class Problem(val waypoint: WaypointsScreenState.Waypoint?, val severe: Boolean, val text: Component)

    companion object {
        private const val Y0: Int = 30
        private val applyTooltip = Tooltip.create(Component.translatable("menu.flightassistant.fms.apply"))

        private var state: WaypointsScreenState = WaypointsScreenState()
        fun reload(flightPlan: FlightPlanComputer) {
            state = WaypointsScreenState.load(flightPlan)
        }

        var problems: MutableList<Problem> = ArrayList()
    }
}