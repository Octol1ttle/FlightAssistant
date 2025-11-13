package ru.octol1ttle.flightassistant.api.util.extensions

import java.util.Locale

fun String.toIntOrNullWithFallback(): Int? {
    if (this == "") {
        return 0
    }
    if (this == "-") {
        return -0
    }

    return this.toIntOrNull()
}

fun String.toFloatOrNullWithFallback(): Float? {
    if (this == "") {
        return 0.0f
    }
    if (this == "-") {
        return -0.0f
    }

    return this.toFloatOrNull()
}

fun String.formatRoot(vararg args: Any?): String {
    return this.format(Locale.ROOT, *args)
}