package ru.octol1ttle.flightassistant.impl.computer.data

import kotlin.math.atan2
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.requireIn

class HudDisplayDataComputer(computers: ComputerBus, private val mc: Minecraft) : Computer(computers) {
    val player: LocalPlayer
        get() = checkNotNull(mc.player)

    val isViewMirrored: Boolean
        get() = mc.options.cameraType.isMirrored

    var lerpedPosition: Vec3 = Vec3.ZERO
        private set
    var lerpedVelocity: Vec3 = Vec3.ZERO
        private set
    var lerpedForwardVelocity: Vec3 = Vec3.ZERO
        private set

    val lerpedAltitude: Double
        get() = lerpedPosition.y

    val roll: Float
        get() = degrees(atan2(-RenderMatrices.worldSpaceMatrix.m10(), RenderMatrices.worldSpaceMatrix.m11())).requireIn(-180.0f..180.0f)

    override fun tick() {
        lerpedPosition = player.getPosition(FATickCounter.partialTick)
        lerpedVelocity = player.getDeltaMovementLerped(FATickCounter.partialTick)
        lerpedForwardVelocity = computers.data.computeForwardVector(lerpedVelocity)
    }

    override fun reset() {
        lerpedPosition = Vec3.ZERO
        lerpedVelocity = Vec3.ZERO
        lerpedForwardVelocity = Vec3.ZERO
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("hud_display_data")
    }
}