package ru.octol1ttle.flightassistant.api.util

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery

class DeltaTimeMultiplierValidator : ComputerQuery.Validator<ControlInput> {
    override fun validate(response: ControlInput) {
        response.deltaTimeMultiplier.throwIfNotInRange(0.001f..Float.MAX_VALUE)
    }
}