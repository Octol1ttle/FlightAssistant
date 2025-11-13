package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.sounds.SoundEvent
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertData(@Deprecated("Waiting for an enhanced priority system (.defineOrder, .before, .after)") val priority: Int, val soundEvent: SoundEvent, val looping: LoopType, val colorSupplier: () -> Int) {
    companion object {
        private fun soundEvent(name: String): SoundEvent {
            return SoundEvent.createVariableRangeEvent(FlightAssistant.id(name))
        }

        val FULL_STALL =
            AlertData(
                0,
                soundEvent("full_stall"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val APPROACHING_STALL =
            AlertData(
                100,
                soundEvent("approaching_stall"),
                LoopType.FADE_IN_OUT,
            ) { FAConfig.display.cautionColor.rgb }
        val PULL_UP =
            AlertData(
                200,
                soundEvent("pull_up"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val SINK_RATE =
            AlertData(
                300,
                soundEvent("sink_rate"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val TERRAIN_AHEAD =
            AlertData(
                400,
                soundEvent("terrain_ahead"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        // TODO: fix hack
        val BELOW_GLIDE_SLOPE_WARNING =
            AlertData(
                500,
                soundEvent("below_glide_slope_warning"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val BELOW_GLIDE_SLOPE =
            AlertData(
                600,
                soundEvent("below_glide_slope"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val DONT_SINK =
            AlertData(
                700,
                soundEvent("dont_sink"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val FORCE_AUTOPILOT_OFF =
            AlertData(
                800,
                soundEvent("autopilot_off"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val PLAYER_AUTOPILOT_OFF =
            AlertData(
                900,
                soundEvent("autopilot_off"),
                LoopType.NONE
            ) { FAConfig.display.warningColor.rgb }
        val MASTER_WARNING =
            AlertData(
                1000,
                soundEvent("master_warning"),
                LoopType.FADE_OUT
            ) { FAConfig.display.warningColor.rgb }
        val CAUTION_TERRAIN =
            AlertData(
                1100,
                soundEvent("caution_terrain"),
                LoopType.NONE
            ) { FAConfig.display.cautionColor.rgb }
        val MINIMUMS_REACHED =
            AlertData(
                1200,
                soundEvent("minimums_reached"),
                LoopType.NONE
            ) { FAConfig.display.cautionColor.rgb }
        val THRUST_LOCKED =
            AlertData(
                1300,
                soundEvent("thrust_locked"),
                LoopType.CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val MASTER_CAUTION =
            AlertData(
                1400,
                soundEvent("master_caution"),
                LoopType.NONE
            ) { FAConfig.display.cautionColor.rgb }
    }

    enum class LoopType {
        FADE_IN_OUT,
        FADE_OUT,
        CONSTANT_VOLUME,
        NONE
    }
}
