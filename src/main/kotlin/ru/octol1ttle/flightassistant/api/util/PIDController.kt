package ru.octol1ttle.flightassistant.api.util

class PIDController(proportional: Double, integral: Double, private val derivative: Double, filterConstant: Int, private val minimumValue: Double, private val maximumValue: Double) {
    private val a0 = proportional + integral * FATickCounter.TICK_TIME
    private val a1 = -proportional

    private var lastLastError = 0.0
    private var lastError = 0.0
    private var error = 0.0

    private var output: Double? = null

    private val a0d = derivative * FATickCounter.TICK_TIME
    private val a1d = -2.0 * derivative * FATickCounter.TICK_TIME
    private val a2d = derivative * FATickCounter.TICK_TIME

    private val tau = derivative / (proportional * filterConstant)
    private val alpha = FATickCounter.TICK_TIME / (2 * tau)

    private var d0 = 0.0
    private var d1 = 0.0
    private var fd0 = 0.0
    private var fd1 = 0.0

    fun calculate(target: Double, current: Double, startOutput: Double): Double {
        lastLastError = lastError
        lastError = error
        error = target - current

        if (output == null) {
            output = startOutput
        }

        output = output!! + a0 * error + a1 * lastError

        if (derivative > 0.0f) {
            d1 = d0
            d0 = a0d * error + a1d * lastError + a2d * lastLastError
            fd1 = fd0
            fd0 = ((alpha) / (alpha + 1)) * (d0 + d1) - ((alpha - 1) / (alpha + 1)) * fd1
            output = output!! + fd0
        }

        output = output!!.coerceIn(minimumValue..maximumValue)

        return output!!
    }
}