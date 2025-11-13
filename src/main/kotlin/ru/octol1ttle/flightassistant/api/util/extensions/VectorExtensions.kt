package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.SharedConstants
import net.minecraft.world.phys.Vec3
import org.joml.Vector2d

fun Vec3.perSecond(): Vec3 {
    return this.scale(SharedConstants.TICKS_PER_SECOND.toDouble())
}

fun Vec3.toVector2d(): Vector2d {
    return Vector2d(this.x, this.z)
}

fun distance2d(x1: Int, z1: Int, x2: Double, z2: Double): Double {
    return Vector2d.distance(x1.toDouble(), z1.toDouble(), x2, z2)
}

fun vec2dFromInts(x: Int, z: Int): Vector2d {
    return Vector2d(x.toDouble(), z.toDouble())
}

fun getProgressOnTrack(track: Vector2d, trackStart: Vector2d, position: Vector2d): Double {
    val trackLengthSquared: Double = track.lengthSquared()
    if (trackLengthSquared == 0.0) {
        return 1.0
    }

    val fromStart: Vector2d = position.sub(trackStart)
    return ((fromStart.x * track.x + fromStart.y * track.y) / trackLengthSquared).coerceIn(0.0..1.0)
}