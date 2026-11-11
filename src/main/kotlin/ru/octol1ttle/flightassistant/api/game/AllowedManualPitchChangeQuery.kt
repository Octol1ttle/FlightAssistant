package ru.octol1ttle.flightassistant.api.game

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery

data class AllowedManualPitchChangeQuery(val pitchChange: Float) : ComputerQuery<ControlInput>()
