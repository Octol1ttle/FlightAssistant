package ru.octol1ttle.flightassistant.impl.computer.data

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.max
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.phys.Vec3
import ru.octol1ttle.flightassistant.FAKeyMappings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.extensions.bottomY
import ru.octol1ttle.flightassistant.api.util.extensions.getLerpedDeltaMovement
import ru.octol1ttle.flightassistant.api.util.extensions.perSecond
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange
import ru.octol1ttle.flightassistant.config.FAConfig

class AirDataComputer(computers: ComputerBus, private val mc: Minecraft) : Computer(computers) {
    val player: LocalPlayer
        get() = checkNotNull(mc.player)
    val flying: Boolean
        get() = player.isFallFlying
    val level: ClientLevel
        get() = checkNotNull(mc.level)

    val position: Vec3
        get() = player.position()
    val x: Double
        get() = position.x
    val z: Double
        get() = position.z
    val altitude: Double
        get() = position.y
    val voidY: Int
        get() = level.bottomY - 64

    private val fallDistance: Double
        get() =
            if (computers.gpws.groundY == null || computers.gpws.groundY!! == Double.MAX_VALUE) Double.MAX_VALUE
//? if >=1.21.5 {
            /*else max(player.fallDistance, altitude - computers.gpws.groundY!!)
*///?} else
            else max(player.fallDistance.toDouble(), altitude - computers.gpws.groundY!!)

    val fallDistanceSafe: Boolean
        get() = player.isInWater || fallDistance <= player.maxFallDistance || isInvulnerableTo(player.damageSources().fall())

    val velocity: Vec3
        get() = player.deltaMovement
    val velocityPerSecond: Vec3
        get() = velocity.perSecond()
    var forwardVelocity: Vec3 = Vec3.ZERO
        private set
    var forwardVelocityPerSecond: Vec3 = Vec3.ZERO
        private set
    var forwardAcceleration: Double = 0.0
        private set

    val pitch: Float
        get() = -player.xRot.throwIfNotInRange(-90.0f..90.0f)
    val yaw: Float
        get() = Mth.wrapDegrees(player.yRot).throwIfNotInRange(-180.0f..180.0f)
    val heading: Float
        get() = (yaw + 180.0f).throwIfNotInRange(0.0f..360.0f)

    val flightPitch: Float
        get() = degrees(asin(velocity.normalize().y).toFloat())
    val flightYaw: Float
        get() = degrees(atan2(-velocity.x, velocity.z).toFloat())

    override fun tick() {
        forwardVelocity = computeForwardVector(velocity)
        forwardVelocityPerSecond = forwardVelocity.perSecond()
        forwardAcceleration = forwardVelocity.length() - computeForwardVector(player.getLerpedDeltaMovement(0.0f)).length()
    }

    fun automationsAllowed(checkFlying: Boolean = true): Boolean {
        if (FAKeyMappings.globalAutomationOverride.isDown) {
            return false
        }
        return (!checkFlying || flying) && (FAConfig.global.automationsAllowedInOverlays || (mc.screen == null && mc.overlay == null))
    }

    fun isInvulnerableTo(source: DamageSource): Boolean {
        if (!FAConfig.safety.considerInvulnerability) {
            return false
        }
//? if >=1.21.2 {
        /*return (player as ru.octol1ttle.flightassistant.mixin.EntityInvoker).invokeIsInvulnerableToBase(source)
*///?} else
        return player.isInvulnerableTo(source)
                || player.abilities.invulnerable && !source.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || player.abilities.mayfly && source.`is`(DamageTypeTags.IS_FALL)
    }

    fun computeForwardVector(vector: Vec3): Vec3 {
        val normalizedLookAngle: Vec3 = player.lookAngle.normalize()
        val normalizedVector: Vec3 = vector.normalize()
        return vector.scale(normalizedLookAngle.dot(normalizedVector).coerceAtLeast(0.0))
    }

    override fun reset() {
        forwardVelocity = Vec3.ZERO
        forwardVelocityPerSecond = Vec3.ZERO
        forwardAcceleration = 0.0
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("air_data")
    }
}