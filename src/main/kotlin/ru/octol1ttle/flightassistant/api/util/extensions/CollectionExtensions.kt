package ru.octol1ttle.flightassistant.api.util.extensions

import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer

/**
 * Filters the current list to try and get an active input with the highest priority in the list.
 * If there are no such inputs, the returned list contains all inputs with the highest priority in the list.
 */
fun List<ControlInput>.getActiveHighestPriority(): List<ControlInput> {
    val activeInput: List<ControlInput> = this.filter { it.active && it.priority.value == this[0].priority.value }
    if (activeInput.any()) {
        return activeInput
    }

    return this.filter { it.priority.value == this[0].priority.value }
}

/**
 * Filters the current list to get alert datas that have the highest priority in the list.
 */
fun List<AlertData>.getHighestPriority(): List<AlertData> {
    return this.filter { it.priority == this[0].priority }
}

/**
 * Filters the current list to only include working computers (enabled and not faulted). If a list contains an object that is not a computer, that object remains in the returned list.
 */
fun <T> List<T>.filterWorking(): List<T> {
    return this.filter { it !is Computer || !it.isDisabledOrFaulted() }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val entry1: T = this[index1]
    this[index1] = this[index2]
    this[index2] = entry1
}