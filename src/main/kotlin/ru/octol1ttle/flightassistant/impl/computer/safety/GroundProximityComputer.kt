package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.abs
import kotlin.math.max
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.extensions.bottomY
import ru.octol1ttle.flightassistant.api.util.inverseMin
import ru.octol1ttle.flightassistant.api.util.throwIfNotInRange
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.PitchComputer

class GroundProximityComputer(computers: ComputerBus) : Computer(computers), FlightController {
    private var groundImpactTime: Double = Double.MAX_VALUE
    var groundImpactStatus: Status = Status.SAFE
        private set
    private var obstacleImpactTime: Double = Double.MAX_VALUE
    var obstacleImpactStatus: Status = Status.SAFE
        private set

    val safeThreshold: Double
        get() = 10.0
    val cautionThreshold: Double
        get() = 7.5
    val warningThreshold: Double
        get() = 3.0
    val recoverThreshold: Double
        get() = 0.75

    var groundY: Double? = null
    val groundOrVoidY: Double
        get() = if (groundY == null || groundY == Double.MAX_VALUE) computers.data.voidY.toDouble()
        else groundY!!

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick() {
        if (computers.chunk.isCurrentLoaded) {
            groundY = computeGroundY()?.throwIfNotInRange(computers.data.level.bottomY.toDouble()..Double.MAX_VALUE)
        }

        if (!computers.data.flying || computers.data.player.isInWater) {
            groundImpactStatus = Status.SAFE
            obstacleImpactStatus = Status.SAFE
            return
        }

        val isRecoveryUnsafe = computeIsRecoveryUnsafe()

        groundImpactTime = computeGroundImpactTime().throwIfNotInRange(0.0..Double.MAX_VALUE)
        groundImpactStatus = computeStatus(groundImpactStatus,
            { computers.data.fallDistanceSafe || computers.data.velocityPerSecond.y > -8.5 || groundImpactTime >= safeThreshold },
            { computers.data.velocityPerSecond.y <= -10 && groundImpactTime <= cautionThreshold },
            { groundImpactTime <= warningThreshold },
            { groundImpactTime <= recoverThreshold }
        )

        val thresholdMultiplier = computeThresholdMultiplier()
        val safe = max(recoverThreshold, safeThreshold * thresholdMultiplier)
        val caution = max(recoverThreshold, cautionThreshold * thresholdMultiplier)
        val warning = max(recoverThreshold, warningThreshold * thresholdMultiplier)

        obstacleImpactTime = computeObstacleImpactTime(computers.data.velocityPerSecond, safe).throwIfNotInRange(0.0..Double.MAX_VALUE)
        val damageOnCollision: Double = computers.data.velocity.horizontalDistance() * 10 - 3
        val invulnerable = computers.data.isInvulnerableTo(computers.data.player.damageSources().flyIntoWall())
        obstacleImpactStatus = computeStatus(obstacleImpactStatus,
            { invulnerable || damageOnCollision < computers.data.player.health * 0.25f || obstacleImpactTime * 1.2f > groundImpactTime || obstacleImpactTime >= safeThreshold * thresholdMultiplier },
            { abs(computers.data.yaw - computers.data.flightYaw) < 10.0f && damageOnCollision >= computers.data.player.health * 0.5f && obstacleImpactTime * 1.1f < groundImpactTime && obstacleImpactTime <= caution },
            { !isRecoveryUnsafe && obstacleImpactTime <= warning },
            { obstacleImpactTime <= recoverThreshold },
        )
    }

    private fun raycast(offset: Vec3): BlockHitResult {
        return computers.data.level.clip(ClipContext(computers.data.position, computers.data.position.add(offset), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, computers.data.player))
    }

    private fun computeGroundY(): Double? {
        if (!computers.chunk.isCurrentLoaded) {
            return groundY
        }
        val playerBoundingBox = computers.data.player.boundingBox
        val minY: Double = computers.data.level.bottomY.toDouble().coerceAtLeast(computers.data.altitude - 2500.0)
        val diffFromMinY = Vec3(0.0, minY - playerBoundingBox.minY, 0.0)
        val collisionResult = Entity.collideBoundingBox(computers.data.player, diffFromMinY, playerBoundingBox, computers.data.level, emptyList())

        val groundY = collisionResult.y + playerBoundingBox.minY
        if (collisionResult.y + playerBoundingBox.maxY == minY || collisionResult == diffFromMinY) {
            return if (groundY > computers.data.level.bottomY) Double.MAX_VALUE else null
        }

        val raycast = raycast(Vec3(0.0, minY - computers.data.position.y, 0.0))
        if (raycast.type == HitResult.Type.BLOCK) {
            return max(raycast.location.y, groundY)
        }
        return groundY
    }

    private fun computeIsRecoveryUnsafe(): Boolean {
        val pitch = max(0, computers.data.pitch.toInt())
        val interval = 15

        for (i in 90 downTo pitch step interval) {
            val offset = Vec3.directionFromRotation(-i.toFloat(), computers.data.yaw)
            val result = raycast(offset.scale(computers.data.velocityPerSecond.length() * recoverThreshold))
            if (result.type != HitResult.Type.BLOCK) {
                 return false
            }
        }

        return true
    }

    private fun computeThresholdMultiplier(): Double {
        val maxRaycastDistance = computers.data.velocityPerSecond.length() * cautionThreshold

        val xzOffsets = listOf(-1.0, 0.0, 1.0)
        val yOffsets = listOf(-1.0, 0.0, 1.0)

        val distances = ArrayList<Double>()
        for (x in xzOffsets) { for (y in yOffsets) { for (z in xzOffsets) {
            val offset = Vec3(x, y, z).normalize().scale(maxRaycastDistance)
            if (offset.lengthSqr() == 0.0) {
                continue
            }
            val result: BlockHitResult = raycast(offset)
            if (result.type == HitResult.Type.BLOCK) {
                distances.add(computers.data.position.distanceTo(result.location))
            }
        }}}

        return distances.average() / maxRaycastDistance
    }

    private fun computeStatus(current: Status, safe: () -> Boolean, caution: () -> Boolean, warning: () -> Boolean, recover: () -> Boolean): Status {
        if (safe()) {
            return Status.SAFE
        }

        var status: Status = current
        while (true) {
            val next = when (status) {
                Status.SAFE -> if (caution()) Status.CAUTION else null
                Status.CAUTION -> if (caution() && warning()) Status.WARNING else null
                Status.WARNING -> if (caution() && warning() && recover()) Status.RECOVER else null
                Status.RECOVER -> null
            }
            if (next == null) {
                break
            }
            status = next
        }

        return status
    }

    private fun computeGroundImpactTime(): Double {
        if (computers.data.velocity.y >= 0.0) {
            return Double.MAX_VALUE
        }
        return max(0.0, computers.data.altitude - groundOrVoidY) / -computers.data.velocityPerSecond.y
    }

    fun computeObstacleImpactTime(velocity: Vec3, lookAheadTime: Double): Double {
        val result: BlockHitResult = raycast(velocity.scale(lookAheadTime))
        if (result.type != HitResult.Type.BLOCK) {
            return Double.MAX_VALUE
        }

        return computers.data.position.distanceTo(result.location) / velocity.length()
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        if (query is PitchComputer.MinimumPitchQuery && (groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateLimitPitch || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleLimitPitch)) {
            query.respond(ControlInput(
                computers.data.pitch.coerceAtMost(15.0f),
                Component.translatable("mode.flightassistant.vertical.terrain_protection"),
                ControlInput.Priority.HIGH
            ))
        }
    }

    private fun getControlInputStatus(status: Status, config: Boolean, forThrust: Boolean): ControlInput.Status? {
        if (!config) return ControlInput.Status.DISABLED
        val armThreshold = if (forThrust) Status.CAUTION else Status.WARNING
        val activeThreshold = if (forThrust) Status.WARNING else Status.RECOVER
        return if (status <= activeThreshold) ControlInput.Status.ACTIVE
        else if (status <= armThreshold) ControlInput.Status.ARMED
        else null
    }

    override fun getThrustInput(): ControlInput? {
        if (computers.data.pitch > 15.0f) {
            return null
        }
        val sinkRateInputStatus = getControlInputStatus(groundImpactStatus, FAConfig.safety.sinkRateAutoThrust, true)
        val terrainInputStatus = getControlInputStatus(obstacleImpactStatus, FAConfig.safety.obstacleAutoThrust, true)
        if (sinkRateInputStatus != null || terrainInputStatus != null) {
            return ControlInput(
                0.0f,
                Component.translatable("mode.flightassistant.thrust.idle"),
                ControlInput.Priority.HIGH,
                status = ControlInput.Status.highest(sinkRateInputStatus, terrainInputStatus)
            )
        }

        return null
    }

    override fun getPitchInput(): ControlInput? {
        val sinkRateInputStatus = getControlInputStatus(groundImpactStatus, FAConfig.safety.sinkRateAutoPitch, false)
        val terrainInputStatus = getControlInputStatus(obstacleImpactStatus, FAConfig.safety.obstacleAutoPitch, false)
        if (sinkRateInputStatus != null || terrainInputStatus != null) {
            val deltaTimeMultiplier: Double = max(1.0, inverseMin(groundImpactTime, obstacleImpactTime) ?: return null)
            return ControlInput(
                90.0f,
                Component.translatable("mode.flightassistant.vertical.terrain_escape"),
                ControlInput.Priority.HIGH,
                deltaTimeMultiplier.toFloat(),
                ControlInput.Status.highest(sinkRateInputStatus, terrainInputStatus)
            )
        }

        return null
    }

    override fun reset() {
        groundImpactTime = Double.MAX_VALUE
        groundImpactStatus = Status.SAFE
        obstacleImpactTime = Double.MAX_VALUE
        obstacleImpactStatus = Status.SAFE
        groundY = Double.MAX_VALUE
    }

    enum class Status {
        RECOVER,
        WARNING,
        CAUTION,
        SAFE;
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("ground_proximity")
    }
}
