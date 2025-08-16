package ru.octol1ttle.flightassistant.impl.computer.safety

import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.AlertCategoryRegistrationCallback
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.ChangeTrackingArrayList
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.applyVolume
import ru.octol1ttle.flightassistant.api.util.extensions.getHighestPriority
import ru.octol1ttle.flightassistant.api.util.extensions.pause
import ru.octol1ttle.flightassistant.api.util.extensions.unpause
import ru.octol1ttle.flightassistant.impl.alert.AlertSoundInstance
import ru.octol1ttle.flightassistant.impl.alert.autoflight.AutoThrustOffAlert
import ru.octol1ttle.flightassistant.impl.alert.autoflight.AutopilotOffAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityCriticalAlert
import ru.octol1ttle.flightassistant.impl.alert.elytra.ElytraDurabilityLowAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.DisplayFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.AlertComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.fault.computer.ComputerFaultAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkExplosiveAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkNoResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.firework.FireworkSlowResponseAlert
import ru.octol1ttle.flightassistant.impl.alert.flight_controls.ProtectionsLostAlert
import ru.octol1ttle.flightassistant.impl.alert.flight_plan.DepartureElevationDisagreeAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.DontSinkAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.PullUpAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.SinkRateAlert
import ru.octol1ttle.flightassistant.impl.alert.gpws.TerrainAheadAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.ApproachingVoidDamageAltitudeAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.NoChunksLoadedAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.ReachedVoidDamageAltitudeAlert
import ru.octol1ttle.flightassistant.impl.alert.navigation.SlowChunkLoadingAlert
import ru.octol1ttle.flightassistant.impl.alert.stall.ApproachingStallAlert
import ru.octol1ttle.flightassistant.impl.alert.stall.FullStallAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.NoThrustSourceAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.ReverseThrustNotSupportedAlert
import ru.octol1ttle.flightassistant.impl.alert.thrust.ThrustLockedAlert
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FireworkComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.HeadingComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.RollComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.ThrustComputer
import ru.octol1ttle.flightassistant.impl.computer.data.AirDataComputer
import ru.octol1ttle.flightassistant.impl.computer.data.HudDisplayDataComputer
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class AlertComputer(computers: ComputerBus, private val soundManager: SoundManager) : Computer(computers) {
    internal var alertsFaulted: Boolean = false
    val categories: MutableList<AlertCategory> = ArrayList()
    private val alertLists: HashMap<AlertData, ChangeTrackingArrayList<Alert>> = HashMap()
    private val sounds: HashMap<AlertData, AlertSoundInstance> = HashMap()

    override fun invokeEvents() {
        registerBuiltin()
        AlertCategoryRegistrationCallback.EVENT.invoker().register(computers, this::register)
    }

    private fun registerBuiltin() {
        register(
            AlertCategory(Component.translatable("alert.flightassistant.alert"))
                .add(AlertComputerFaultAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.autoflight"))
                .add(ComputerFaultAlert(computers, AutoFlightComputer.ID, Component.translatable("alert.flightassistant.autoflight.fault")))
                .add(ComputerFaultAlert(computers, PitchComputer.ID, Component.translatable("alert.flightassistant.autoflight.pitch_fault")))
                .add(ComputerFaultAlert(computers, HeadingComputer.ID, Component.translatable("alert.flightassistant.autoflight.heading_fault")))
                .add(ComputerFaultAlert(computers, RollComputer.ID, Component.translatable("alert.flightassistant.autoflight.roll_fault")))
                .add(AutopilotOffAlert(computers))
                .add(AutoThrustOffAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.elytra"))
                .add(ComputerFaultAlert(computers, ElytraStatusComputer.ID, Component.translatable("alert.flightassistant.elytra.fault")))
                .add(ElytraDurabilityCriticalAlert(computers))
                .add(ElytraDurabilityLowAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.fault.hud"))
                .add(ComputerFaultAlert(computers, HudDisplayDataComputer.ID, Component.translatable("alert.flightassistant.fault.hud.data")))
                .addAll(HudDisplayHost.identifiers().map { DisplayFaultAlert(computers, it) })
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.firework"))
                .add(ComputerFaultAlert(computers, FireworkComputer.ID, Component.translatable("alert.flightassistant.firework.fault")))
                .add(FireworkExplosiveAlert(computers, InteractionHand.MAIN_HAND))
                .add(FireworkExplosiveAlert(computers, InteractionHand.OFF_HAND))
                .add(FireworkNoResponseAlert(computers))
                .add(FireworkSlowResponseAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.flight_controls"))
                .add(
                    ComputerFaultAlert(
                        computers, PitchComputer.ID, Component.translatable("alert.flightassistant.flight_controls.pitch_fault"), listOf(
                            Component.translatable("alert.flightassistant.flight_controls.pitch_fault.use_manual_pitch"),
                )))
                .add(ProtectionsLostAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.flight_plan"))
                .add(ComputerFaultAlert(computers, FlightPlanComputer.ID, Component.translatable("alerts.flightassistant.flight_plan.fault")))
                .add(DepartureElevationDisagreeAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.gpws"))
                .add(ComputerFaultAlert(computers, GroundProximityComputer.ID, Component.translatable("alert.flightassistant.gpws.fault")))
                .add(PullUpAlert(computers))
                .add(SinkRateAlert(computers))
                .add(TerrainAheadAlert(computers))
                .add(DontSinkAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.navigation"))
                .add(ComputerFaultAlert(computers, AirDataComputer.ID, Component.translatable("alert.flightassistant.navigation.air_data_fault"), data = AlertData.MASTER_WARNING))
                .add(ComputerFaultAlert(computers, ChunkStatusComputer.ID, Component.translatable("alert.flightassistant.navigation.chunk_status_fault")))
                .add(ComputerFaultAlert(computers, VoidProximityComputer.ID, Component.translatable("alert.flightassistant.navigation.void_proximity_fault")))
                .add(ReachedVoidDamageAltitudeAlert(computers))
                .add(ApproachingVoidDamageAltitudeAlert(computers))
                .add(NoChunksLoadedAlert(computers))
                .add(SlowChunkLoadingAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.stall"))
                .add(ComputerFaultAlert(computers, StallComputer.ID, Component.translatable("alert.flightassistant.stall.detection_fault")))
                .add(FullStallAlert(computers))
                .add(ApproachingStallAlert(computers))
        )
        register(
            AlertCategory(Component.translatable("alert.flightassistant.thrust"))
                .add(ComputerFaultAlert(computers, ThrustComputer.ID, Component.translatable("alert.flightassistant.thrust.fault")))
                .add(ThrustLockedAlert(computers))
                .add(NoThrustSourceAlert(computers))
                .add(ReverseThrustNotSupportedAlert(computers))
        )
    }

    fun register(category: AlertCategory) {
        if (categories.contains(category)) {
            throw IllegalArgumentException("Already registered alert category: ${category.categoryText.string}")
        }

        categories.add(category)
    }

    fun hideCurrentAlert() {
        for (category: AlertCategory in categories) {
            if (category.activeAlerts.isEmpty()) {
                continue
            }
            val alert: Alert = category.activeAlerts.removeAt(0)
            category.ignoredAlerts.add(alert)
            alert.onHide()
            break
        }
    }

    fun showHiddenAlert() {
        for (category: AlertCategory in categories) {
            if (category.ignoredAlerts.isEmpty()) {
                continue
            }
            category.ignoredAlerts.removeAt(0)
            break
        }
    }

    override fun tick() {
        updateAlerts()
        if (computers.data.player.isDeadOrDying || !computers.data.flying) {
            tickSoundsAndStopInactive(true)
            return
        }
        tickSoundsAndStopInactive()
        startNewSounds()
        stopOutPrioritizedAlerts()
    }

    private fun updateAlerts() {
        for (category: AlertCategory in categories) {
            category.updateActiveAlerts(computers)
        }

        categories.sortBy { it.getHighestPriority() ?: Int.MAX_VALUE }

        alertLists.values.forEach(ChangeTrackingArrayList<*>::startTracking)

        for (alert: Alert in categories.flatMap { it.activeAlerts } ) {
            alertLists.computeIfAbsent(alert.data) { return@computeIfAbsent ChangeTrackingArrayList() }.add(alert)
        }
    }

    private fun tickSoundsAndStopInactive(force: Boolean = false) {
        val iterator = sounds.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            repeat(FATickCounter.ticksPassed) {
                entry.value.tick()
                soundManager.applyVolume(entry.value)
            }

            if (force || alertLists[entry.key]?.isEmpty() != false) {
                entry.value.setLooping(false, soundManager)
                if (entry.value.fadeOut(FATickCounter.ticksPassed)) {
                    iterator.remove()
                }
                soundManager.applyVolume(entry.value)
            }
        }
    }

    private fun startNewSounds() {
        val newDatas: List<AlertData> = alertLists.filterValues { list -> list.hasNewElements { it.getAlertMethod().audio() } || list.any { !sounds.containsKey(it.data) && it.getAlertMethod().audio() } }.keys.sortedBy { it.priority }
        val newHighestPriorityDatas: List<AlertData> = newDatas.getHighestPriority()
        val activeHighestPriority: Int = sounds.keys.minByOrNull { it.priority }?.priority ?: Int.MAX_VALUE

        var anyStartedThisTick = false
        for (data: AlertData in newHighestPriorityDatas) {
            if (data.priority > activeHighestPriority) {
                break
            }

            val existing: AlertSoundInstance? = sounds[data]
            if (existing == null || (!existing.isLooping && existing.age > 60)) {
                if (existing != null) {
                    soundManager.stop(existing)
                }

                val instance = AlertSoundInstance(computers.data.player, data)
                sounds[data] = instance
                if (!anyStartedThisTick) {
                    soundManager.play(instance)
                } else if (instance.isLooping) {
                    instance.silence()
                    soundManager.play(instance)
                    soundManager.pause(instance)
                }

                anyStartedThisTick = true
            }
        }
    }

    private fun stopOutPrioritizedAlerts() {
        var interrupt = false
        for (entry in sounds.entries.sortedBy { it.key.priority }) {
            if (!interrupt) {
                interrupt = true
                if (entry.value.isLooping) {
                    soundManager.unpause(entry.value)
                }
                continue
            }

            if (entry.value.isLooping) {
                soundManager.pause(entry.value)
            } else {
                entry.value.fadeOut(FATickCounter.ticksPassed)
            }
        }
    }

    override fun reset() {
        categories.forEach { it.activeAlerts.clear() }
        categories.forEach { it.ignoredAlerts.clear() }
        sounds.values.forEach { soundManager.stop(it) }
        sounds.clear()
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("alert")
    }
}
