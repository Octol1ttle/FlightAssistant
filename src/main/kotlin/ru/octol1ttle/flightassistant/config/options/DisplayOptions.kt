package ru.octol1ttle.flightassistant.config.options

import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.SerialEntry
import java.awt.Color
import net.minecraft.network.chat.Component

class DisplayOptions {
    @SerialEntry
    var frameWidth: Float = 0.6f
    @SerialEntry
    var frameHeight: Float = 0.5f

    @SerialEntry
    var primaryColor: Color = Color.GREEN
    @SerialEntry
    var secondaryColor: Color = Color.WHITE
    @SerialEntry
    var primaryAdvisoryColor: Color = Color.CYAN
    @SerialEntry
    var secondaryAdvisoryColor: Color = Color.MAGENTA
    @SerialEntry
    var cautionColor: Color = Color.YELLOW
    @SerialEntry
    var warningColor: Color = Color.RED

    @SerialEntry
    var showAttitude: AttitudeDisplayMode = AttitudeDisplayMode.HORIZON_AND_LADDER
    @SerialEntry
    var attitudeDegreeStep: Int = 15
    @SerialEntry
    var drawPitchOutsideFrame: Boolean = true

    @SerialEntry
    var showHeadingReading: Boolean = true
    @SerialEntry
    var showHeadingScale: Boolean = true

    @SerialEntry
    var showSpeedReading: Boolean = true
    @SerialEntry
    var showSpeedScale: Boolean = true

    @SerialEntry
    var showAltitudeReading: Boolean = true
    @SerialEntry
    var showAltitudeScale: Boolean = true
    @SerialEntry
    var showRadarAltitude: Boolean = true

    @SerialEntry
    var showFlightPathVector: Boolean = true

    @SerialEntry @Deprecated("does anyone use this??")
    var flightPathVectorSize: Float = 1.0f

    @SerialEntry
    var showElytraDurability: Boolean = true
    @SerialEntry
    var elytraDurabilityUnits: DurabilityUnits = DurabilityUnits.TIME

    @SerialEntry
    var showCoordinates: Boolean = true

    @SerialEntry
    var showGroundSpeed: Boolean = true
    @SerialEntry
    var showVerticalSpeed: Boolean = true

    @SerialEntry
    var showAlerts: Boolean = true
    @SerialEntry
    var showStatusMessages: Boolean = true

    @SerialEntry
    var showAutomationModes: Boolean = true
    @SerialEntry
    var showFlightDirectors: Boolean = true
    @SerialEntry
    var showCourseDeviation: Boolean = true

    internal fun setMinimal(): DisplayOptions {
        this.showAttitude = AttitudeDisplayMode.DISABLED
        this.showHeadingScale = false
        this.showSpeedReading = false
        this.showSpeedScale = false
        this.showAltitudeScale = false
        this.showFlightPathVector = false
        this.showVerticalSpeed = false
        this.showCourseDeviation = false
        return this
    }

    internal fun setDisabled(): DisplayOptions {
        this.showAttitude = AttitudeDisplayMode.DISABLED
        this.showHeadingReading = false
        this.showHeadingScale = false
        this.showSpeedReading = false
        this.showSpeedScale = false
        this.showAltitudeReading = false
        this.showAltitudeScale = false
        this.showRadarAltitude = false
        this.showFlightPathVector = false
        this.showElytraDurability = false
        this.showCoordinates = false
        this.showGroundSpeed = false
        this.showVerticalSpeed = false
        this.showAlerts = false
        this.showStatusMessages = false
        this.showAutomationModes = false
        this.showFlightDirectors = false
        this.showCourseDeviation = false
        return this
    }

    enum class AttitudeDisplayMode : NameableEnum {
        HORIZON_AND_LADDER {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.attitude.show.horizon_and_ladder")
        },
        HORIZON_ONLY {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.attitude.show.horizon_only")
        },
        DISABLED {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.attitude.show.disabled")
        };
    }

    enum class DurabilityUnits : NameableEnum {
        RAW {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.elytra_durability.units.raw")
        },
        PERCENTAGE {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.elytra_durability.units.percentage")
        },
        TIME {
            override fun getDisplayName(): Component =
                Component.translatable("config.flightassistant.option.display.elytra_durability.units.time")
        };
    }
}
