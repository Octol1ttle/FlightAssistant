package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.minecraft.network.chat.Component

class AlertOptions {
    @SerialEntry
    var soundVolume: Float = 1.0f

    @SerialEntry
    var allowSoundLooping: Boolean = true

    @SerialEntry
    var elytraDurabilityAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var stallAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var voidDamageAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var sinkRateAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var obstacleAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var belowGlideSlopeAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var altitudeLoss: Boolean = true

    @SerialEntry
    var fireworkExplosive: Boolean = true

    @SerialEntry
    var fireworkResponseAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    @SerialEntry
    var chunkLoadingAlertSeverity: AlertSeverity = AlertSeverity.WARNING_AND_DANGER

    enum class AlertSeverity : NameableEnum {
        WARNING_AND_DANGER {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.alerts.severity.warning_and_danger")
        },
        WARNING_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.alerts.severity.warning_only")
        },
        DANGER_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.alerts.severity.danger_only")
        },
        DISABLED {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.alerts.severity.disabled")
        };

        fun warning(): Boolean {
            return this == WARNING_AND_DANGER || this == WARNING_ONLY
        }

        fun danger(): Boolean {
            return this == WARNING_AND_DANGER || this == DANGER_ONLY
        }
    }
}