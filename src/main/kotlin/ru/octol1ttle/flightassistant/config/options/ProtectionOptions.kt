package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.config.v2.api.SerialEntry

class ProtectionOptions {
    @SerialEntry
    var considerInvulnerability: Boolean = true

    @SerialEntry
    var elytraAutoOpen: Boolean = true

    @SerialEntry
    var stallLimitPitch: Boolean = true

    @SerialEntry
    var stallAutoThrust: Boolean = true

    @SerialEntry
    var voidLimitPitch: Boolean = true

    @SerialEntry
    var voidAutoRecover: Boolean = true

    @SerialEntry
    var sinkRateLimitPitch: Boolean = true

    @SerialEntry
    var sinkRateLimitThrust: Boolean = true

    @SerialEntry
    var sinkRateAutoPitch: Boolean = true

    @SerialEntry
    var obstacleLimitThrust: Boolean = true

    @SerialEntry
    var obstacleAutoPitch: Boolean = true

    @SerialEntry
    var fireworkLockExplosive: Boolean = true

    @SerialEntry
    var fireworkLockObstacle: Boolean = true

    companion object {
        val DISABLED = setDisabled()

        private fun setDisabled(): ProtectionOptions {
            val options = ProtectionOptions()

            options.elytraAutoOpen = false
            options.stallLimitPitch = false
            options.stallAutoThrust = false
            options.voidLimitPitch = false
            options.voidAutoRecover = false
            options.sinkRateLimitPitch = false
            options.sinkRateLimitThrust = false
            options.sinkRateAutoPitch = false
            options.obstacleLimitThrust = false
            options.obstacleAutoPitch = false
            options.fireworkLockExplosive = false
            options.fireworkLockObstacle = false

            return options
        }
    }
}
