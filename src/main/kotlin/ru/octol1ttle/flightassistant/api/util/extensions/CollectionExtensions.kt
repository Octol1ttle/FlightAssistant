package ru.octol1ttle.flightassistant.api.util.extensions

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

/**
 * Filters the current list to try and get an active input with the highest priority in the list.
 * If there are no such inputs, the returned list contains all inputs with the highest priority in the list.
 * If there are no inputs whatsoever, the original list is returned
 */
fun Collection<ControlInput>.getActiveHighestPriority(): Collection<ControlInput> {
    if (this.isEmpty()) {
        return this
    }

    // TODO: Disabled inputs should not be submitted in the first place
    val noDisabled = this.filter { it.status != ControlInput.Status.DISABLED }
    val activeInput: List<ControlInput> = noDisabled.filter { it.status == ControlInput.Status.ACTIVE && it.priority.value == noDisabled[0].priority.value }
    if (activeInput.any()) {
        return activeInput
    }

    return noDisabled.filter { it.priority.value == noDisabled[0].priority.value }
}
