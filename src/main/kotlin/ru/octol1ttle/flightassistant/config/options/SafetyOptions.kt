package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.minecraft.network.chat.Component

class SafetyOptions {
    @SerialEntry
    var alertVolume: Float = 1.0f

    @SerialEntry
    var considerInvulnerability: Boolean = true

    @SerialEntry
    var elytraDurabilityAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var elytraAutoOpen: Boolean = true
    @SerialEntry @Deprecated("Functionality not possible in vanilla")
    var elytraCloseUnderwater: Boolean = true

    @SerialEntry
    var stallAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var stallAlertMethod: AlertMethod = AlertMethod.SCREEN_AND_AUDIO
    @SerialEntry
    var stallLimitPitch: Boolean = true
    @SerialEntry
    var stallAutoThrust: Boolean = true

    @SerialEntry
    var voidAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var voidLimitPitch: Boolean = true
    @SerialEntry
    var voidAutoThrust: Boolean = true // TODO: merge to AutoRecover since these settings rely on each other
    @SerialEntry
    var voidAutoPitch: Boolean = true

    // TODO: Early/Medium/Late setting for GPWS reaction
    @SerialEntry
    var sinkRateAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var sinkRateAlertMethod: AlertMethod = AlertMethod.SCREEN_AND_AUDIO
    @SerialEntry
    var sinkRateLimitPitch: Boolean = true
    @SerialEntry
    var sinkRateAutoThrust: Boolean = true
    @SerialEntry
    var sinkRateAutoPitch: Boolean = true

    @SerialEntry
    var obstacleAlertMode: AlertMode = AlertMode.WARNING_AND_CAUTION
    @SerialEntry
    var obstacleAlertMethod: AlertMethod = AlertMethod.SCREEN_AND_AUDIO
    @SerialEntry
    var obstacleLimitPitch: Boolean = false // TODO: remove, limiting pitch is often dangerous, especially in the Nether
    @SerialEntry
    var obstacleAutoThrust: Boolean = true
    @SerialEntry
    var obstacleAutoPitch: Boolean = true

    @SerialEntry
    var fireworkExplosiveAlert: Boolean = true
    @SerialEntry
    var fireworkLockExplosive: Boolean = true

    internal fun setDisabled(): SafetyOptions {
        this.alertVolume = 0.0f

        this.elytraDurabilityAlertMode = AlertMode.DISABLED
        this.elytraAutoOpen = false
        this.elytraCloseUnderwater = false

        this.stallAlertMode = AlertMode.DISABLED
        this.stallLimitPitch = false
        this.stallAutoThrust = false
        
        this.voidAlertMode = AlertMode.DISABLED
        this.voidLimitPitch = false
        this.voidAutoThrust = false
        this.voidAutoPitch = false

        this.sinkRateAlertMode = AlertMode.DISABLED
        this.sinkRateLimitPitch = false
        this.sinkRateAutoThrust = false
        this.sinkRateAutoPitch = false

        this.obstacleAlertMode = AlertMode.DISABLED
        this.obstacleLimitPitch = false
        this.obstacleAutoThrust = false
        this.obstacleAutoPitch = false

        this.fireworkExplosiveAlert = false
        this.fireworkLockExplosive = false
        return this
    }

    enum class AlertMode : NameableEnum {
        WARNING_AND_CAUTION {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_mode.warning_and_caution")
        },
        WARNING {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_mode.warning")
        },
        CAUTION {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_mode.caution")
        },
        DISABLED {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_mode.disabled")
        };

        fun warning(): Boolean {
            return this == WARNING_AND_CAUTION || this == WARNING
        }

        fun caution(): Boolean {
            return this == WARNING_AND_CAUTION || this == CAUTION
        }
    }

    enum class AlertMethod : NameableEnum {
        SCREEN_AND_AUDIO {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_method.screen_and_audio")
        },
        AUDIO_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_method.audio_only")
        },
        SCREEN_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.safety.alert_method.screen_only")
        };

        fun screen(): Boolean {
            return this == SCREEN_AND_AUDIO || this == SCREEN_ONLY
        }

        fun audio(): Boolean {
            return this == SCREEN_AND_AUDIO || this == AUDIO_ONLY
        }

        companion object {
            fun min(a: AlertMethod, b: AlertMethod): AlertMethod {
                return if (a <= b) a else b
            }
        }
    }

    companion object {
        val DISABLED: SafetyOptions = SafetyOptions().setDisabled()
    }
}
