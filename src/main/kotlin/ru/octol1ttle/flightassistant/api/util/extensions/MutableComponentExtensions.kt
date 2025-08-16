package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

fun MutableComponent.appendWithSeparation(other: Component, separator: String = " ") {
    if (this.siblings.isNotEmpty()) {
        this.append(separator)
    }
    this.append(other)
}

fun MutableComponent.appendWithSeparation(other: Component, separator: Component) {
    if (this.siblings.isNotEmpty()) {
        this.append(separator)
    }
    this.append(other)
}

fun MutableComponent.setColor(color: Int): MutableComponent {
    this.style = Style.EMPTY.withColor(color)
    return this
}
