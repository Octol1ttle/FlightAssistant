package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import java.awt.Color

class HudOptions {
    @SerialEntry
    var frameWidth: Float = 0.6f

    @SerialEntry
    var frameHeight: Float = 0.5f

    @SerialEntry
    var allowDrawingOutsideFrame: Boolean = true

    @SerialEntry
    var mainColor: Color = Color.GREEN

    @SerialEntry
    var secondaryColor: Color = Color.WHITE

    // TODO: better naming/usage...
    @SerialEntry
    var primaryAdvisoryColor: Color = Color.CYAN

    @SerialEntry
    var secondaryAdvisoryColor: Color = Color.MAGENTA

    @SerialEntry
    var warningColor: Color = Color.YELLOW

    @SerialEntry
    var dangerColor: Color = Color.RED

    @SerialEntry
    var showAttitude: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var attitudeElements: AttitudeElements = AttitudeElements.HORIZON_AND_LADDER

    @SerialEntry
    var attitudeLadderDegreeInterval: Int = 15

    @SerialEntry
    var showSpeedReading: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showSpeedScale: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showGroundSpeed: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showVerticalSpeed: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showAltitudeReading: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showAltitudeScale: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showHeightAboveGround: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showHeadingReading: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showHeadingScale: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showElytraDurability: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var elytraDurabilityUnits: DurabilityUnits = DurabilityUnits.TIME

    @SerialEntry
    var showAutomationModes: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showFlightDirectors: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showCourseDeviation: DisplayCondition = DisplayCondition.WHEN_FLYING

    @SerialEntry
    var showFlightPathVector: Boolean = true

    @SerialEntry
    var showCoordinates: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    @SerialEntry
    var showAlerts: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    // TODO: split into category with each message being configurable
    @SerialEntry
    var showStatusMessages: DisplayCondition = DisplayCondition.WHEN_ELYTRA_EQUIPPED

    enum class DisplayCondition : NameableEnum {
        ALWAYS {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.display.always")
        },
        WHEN_ELYTRA_EQUIPPED {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.display.when_elytra_equipped")
        },
        WHEN_FLYING {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.display.when_flying")
        },
        NEVER {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.display.never")
        };

        fun allowed(): Boolean {
            if (this == NEVER) {
                return false
            }

            val player: LocalPlayer = checkNotNull(mc.player)

            if (player.isFallFlying) {
                return this <= WHEN_FLYING
            }

            if (!player.abilities.mayfly) {
//? if >=1.21.2 {
                /*for (slot: net.minecraft.world.entity.EquipmentSlot in net.minecraft.world.entity.EquipmentSlot.VALUES) {
                    if (player.getItemBySlot(slot).has(net.minecraft.core.component.DataComponents.GLIDER)) {
                        return this <= WHEN_ELYTRA_EQUIPPED
                    }
                }
*///?} else {
                if (player.allSlots.any { stack -> stack.item is net.minecraft.world.item.ElytraItem }) {
                    return this <= WHEN_ELYTRA_EQUIPPED
                }
//?}
            }

            return this == ALWAYS
        }
    }

    enum class AttitudeElements : NameableEnum {
        HORIZON_AND_LADDER {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.attitude.elements.horizon_and_ladder")
        },
        HORIZON_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.attitude.elements.horizon_only")
        }
    }

    enum class DurabilityUnits : NameableEnum {
        RAW {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.elytra_durability.units.raw")
        },
        PERCENTAGE {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.elytra_durability.units.percentage")
        },
        TIME {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.hud.elytra_durability.units.time")
        };
    }
}