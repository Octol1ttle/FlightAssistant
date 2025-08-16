package ru.octol1ttle.flightassistant.impl.computer.data

import kotlin.math.asin
import kotlin.math.max
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.extensions.bottomY
import ru.octol1ttle.flightassistant.api.util.requireIn
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
    val altitude: Double
        get() = position.y
    val voidY: Int
        get() = level.bottomY - 64
    var groundY: Double? = null
        private set(value) {
            field = value?.requireIn(level.bottomY.toDouble()..Double.MAX_VALUE)
        }

    private val fallDistance: Double
        get() =
            if (groundY == null || groundY!! == Double.MAX_VALUE) Double.MAX_VALUE
//? if >=1.21.5 {
            /*else max(player.fallDistance, altitude - groundY!!)
*///?} else
            else max(player.fallDistance.toDouble(), altitude - groundY!!)

    val fallDistanceSafe: Boolean
        get() = player.isInWater || fallDistance <= player.maxFallDistance || isInvulnerableTo(player.damageSources().fall())

    val velocity: Vec3
        get() = player.deltaMovement
    var forwardVelocity: Vec3 = Vec3.ZERO
        private set
    var forwardAcceleration: Double = 0.0
        private set

    val pitch: Float
        get() = -player.xRot.requireIn(-90.0f..90.0f)
    val yaw: Float
        get() = Mth.wrapDegrees(player.yRot).requireIn(-180.0f..180.0f)
    val heading: Float
        get() = (yaw + 180.0f).requireIn(0.0f..360.0f)

    val flightPitch: Float
        get() = degrees(asin(velocity.normalize().y).toFloat())

    val isCurrentChunkLoaded: Boolean
        get() = level.chunkSource.hasChunk(player.chunkPosition().x, player.chunkPosition().z)

    override fun tick() {
        groundY = computeGroundLevel()
        forwardVelocity = computeForwardVector(velocity)
        forwardAcceleration = forwardVelocity.length() - computeForwardVector(player.getDeltaMovementLerped(0.0f)).length()
    }

    fun automationsAllowed(checkFlying: Boolean = true): Boolean {
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

    private fun computeGroundLevel(): Double? {
        if (!isCurrentChunkLoaded) {
            return groundY
        }

        val minY: Double = level.bottomY.toDouble().coerceAtLeast(altitude - 2500)
        val result: BlockHitResult = level.clip(
            ClipContext(
                position,
                position.with(Direction.Axis.Y, minY),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                player
            )
        )
        if (result.type == HitResult.Type.MISS) {
            return if (result.location.y > level.bottomY) Double.MAX_VALUE else null
        }
        return result.location.y
    }

    fun computeForwardVector(vector: Vec3): Vec3 {
        val normalizedLookAngle: Vec3 = player.lookAngle.normalize()
        val normalizedVector: Vec3 = vector.normalize()
        return vector.scale(normalizedLookAngle.dot(normalizedVector).coerceAtLeast(0.0))
    }

    override fun reset() {
        groundY = null
        forwardVelocity = Vec3.ZERO
        forwardAcceleration = 0.0
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("air_data")
    }
}