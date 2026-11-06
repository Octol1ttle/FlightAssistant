package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.minecraft.network.chat.Component

class GlobalOptions {
    @SerialEntry
    internal var modEnabled: Boolean = true

    @SerialEntry
    internal var showHud: Boolean = true

    @SerialEntry
    internal var alerts: AlertMode = AlertMode.ENABLED_HUD_SOUND

    @SerialEntry
    internal var allowAutomations: Boolean = true

    @SerialEntry
    internal var allowAutomationsInGUI: Boolean = false

    @SerialEntry
    internal var flightProtections: Boolean = true

    @SerialEntry
    internal var autopilotResistanceThreshold: Float = 0.5f

    enum class AlertMode : NameableEnum {
        ENABLED_HUD_SOUND {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.general.alerts.enabled_hud_sound")
        },
        ENABLED_HUD_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.general.alerts.enabled_hud_only")
        },
        ENABLED_SOUND_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.general.alerts.enabled_sound_only")
        },
        DISABLED {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.general.alerts.disabled")
        };

        fun hud(): Boolean {
            return this == ENABLED_HUD_SOUND || this == ENABLED_HUD_ONLY
        }

        fun sound(): Boolean {
            return this == ENABLED_HUD_SOUND || this == ENABLED_SOUND_ONLY
        }
    }
}
