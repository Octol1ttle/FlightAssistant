package ru.octol1ttle.flightassistant.api.util

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.min

fun degrees(value: Float): Float {
    return (value * (180.0f / PI)).toFloat()
}

fun degrees(value: Double): Double {
    return (value * (180.0 / PI))
}

fun radians(value: Float): Float {
    return (value * (PI / 180.0)).toFloat()
}

fun radians(value: Double): Double {
    return (value * (PI / 180.0))
}

fun Float.throwIfNotFinite(): Float {
    require(this.isFinite()) {
        "Float value invalid; expected finite value, got $this"
    }

    return this
}

fun Float.throwIfNotInRange(range: ClosedFloatingPointRange<Float>): Float {
    this.throwIfNotFinite()
    
    require(range.contains(this)) {
        "Float value invalid; expected [${range.start}; ${range.endInclusive}], got $this"
    }

    return this
}

fun Double.throwIfNotFinite(): Double {
    require(this.isFinite()) {
        "Double value invalid; expected finite value, got $this"
    }

    return this
}

fun Double.throwIfNotInRange(range: ClosedRange<Double>): Double {
    this.throwIfNotFinite()

    require(range.contains(this)) {
        "Double value invalid; expected [${range.start}; ${range.endInclusive}], got $this"
    }

    return this
}

fun findShortestPath(from: Float, to: Float, valueRange: Float): Float {
    var diff: Float = (to - from) % valueRange

    if (diff >= valueRange * 0.5) {
        diff -= valueRange
    }
    if (diff < valueRange * -0.5) {
        diff += valueRange
    }

    return diff
}

fun pointsToDirection(targetX: Double, targetZ: Double, originX: Double, originZ: Double): Double {
    return degrees(atan2(-(targetX - originX), targetZ - originZ))
}

fun inverseMin(a: Double, b: Double): Double? {
    if (a == 0.0 && b == 0.0) {
        return null
    }
    if (a == 0.0) {
        return 1.0 / b
    }
    if (b == 0.0) {
        return 1.0 / a
    }

    return 1.0 / min(a, b)
}