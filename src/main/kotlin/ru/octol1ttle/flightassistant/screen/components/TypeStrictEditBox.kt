package ru.octol1ttle.flightassistant.screen.components

import com.google.common.base.Predicates
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import ru.octol1ttle.flightassistant.api.util.extensions.font
import java.util.function.Consumer
import java.util.function.Predicate

class TypeStrictEditBox<T>(x: Int, y: Int, width: Int, height: Int, initialValue: T, onValueChange: Consumer<T>, convertFunction: (String) -> T?, filter: Predicate<T> = Predicates.alwaysTrue<T>()) : EditBox(font, x, y, width, height, Component.empty()) {
    init {
        this.value = initialValue.toString()
        this.setFilter {
            val value: T? = convertFunction.invoke(it)
            value != null && filter.test(value)
        }
        this.setResponder { onValueChange.accept(convertFunction.invoke(it)!!) }
        this.moveCursorToStart()
    }

    companion object {
        val XZ_FILTER = { xz: Int -> xz >= -Level.MAX_LEVEL_SIZE && xz < Level.MAX_LEVEL_SIZE }
        val Y_FILTER = { y: Int -> y >= Level.MIN_ENTITY_SPAWN_Y && y < Level.MAX_ENTITY_SPAWN_Y }
    }
}