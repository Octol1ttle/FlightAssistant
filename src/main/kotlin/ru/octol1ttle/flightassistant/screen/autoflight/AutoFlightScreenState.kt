package ru.octol1ttle.flightassistant.screen.autoflight

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.DirectCoordinatesLateralMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.HeadingLateralMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.PitchVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.SelectedAltitudeVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.SpeedThrustMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.VerticalProfileThrustMode

class AutoFlightScreenState {
    var thrustMode: ThrustMode = ThrustMode.SPEED
    var targetSpeed: Int = 15
    var climbThrustPercent: Int = 100
    var descendThrustPercent: Int = 0

    var verticalMode: VerticalMode = VerticalMode.PITCH
    var targetPitch: Float = 5.0f
    var targetAltitude: Int = 0

    var lateralMode: LateralMode = LateralMode.HEADING
    var targetHeading: Int = 360
    var targetCoordinatesX: Int = 0
    var targetCoordinatesZ: Int = 0

    fun apply(autoFlight: AutoFlightComputer) {
        autoFlight.selectedThrustMode = when (thrustMode) {
            ThrustMode.SPEED -> SpeedThrustMode(targetSpeed)
            ThrustMode.VERTICAL_PROFILE -> VerticalProfileThrustMode(climbThrustPercent / 100.0f, descendThrustPercent / 100.0f)
            ThrustMode.FLIGHT_PLAN -> null
        }
        autoFlight.selectedVerticalMode = when (verticalMode) {
            VerticalMode.PITCH -> PitchVerticalMode(targetPitch)
            VerticalMode.ALTITUDE -> SelectedAltitudeVerticalMode(targetAltitude)
            VerticalMode.FLIGHT_PLAN -> null
        }
        autoFlight.selectedLateralMode = when (lateralMode) {
            LateralMode.HEADING -> HeadingLateralMode(targetHeading)
            LateralMode.COORDINATES -> DirectCoordinatesLateralMode(targetCoordinatesX, targetCoordinatesZ)
            LateralMode.FLIGHT_PLAN -> null
        }
    }

    enum class ThrustMode(@JvmField val displayName: Component) : NameableEnum {
        SPEED(Component.translatable("menu.flightassistant.autoflight.thrust.speed")),
        VERTICAL_PROFILE(Component.translatable("menu.flightassistant.autoflight.thrust.vertical_profile")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.thrust.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }

    enum class VerticalMode(@JvmField val displayName: Component) : NameableEnum {
        PITCH(Component.translatable("menu.flightassistant.autoflight.vertical.pitch")),
        ALTITUDE(Component.translatable("menu.flightassistant.autoflight.vertical.altitude")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.vertical.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }

    enum class LateralMode(@JvmField val displayName: Component) : NameableEnum {
        HEADING(Component.translatable("menu.flightassistant.autoflight.lateral.heading")),
        COORDINATES(Component.translatable("menu.flightassistant.autoflight.lateral.coordinates")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.lateral.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }
}