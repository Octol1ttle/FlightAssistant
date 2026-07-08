package ru.octol1ttle.flightassistant.screen.components

import com.google.common.base.Predicates
import java.util.function.Consumer
import java.util.function.Predicate
import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TypeStrictEditBox<T>(x: Int, y: Int, width: Int, height: Int, initialValue: T, onValueChange: Consumer<T>, convertFunction: (String) -> T?, filter: Predicate<T> = Predicates.alwaysTrue<T>()) : EditBox(font, x, y, width, height, Component.empty()) {
    init {
        this.value = initialValue.toString()
//? if <26.2 {
        this.setFilter {
            val value: T? = convertFunction.invoke(it)
            value != null && filter.test(value)
        }
//?}
        this.setResponder {
//? if <26.2 {
            onValueChange.accept(convertFunction.invoke(it)!!)
//?} else {
            val v = convertFunction.invoke(it)
            if (v != null && filter.test(v)) onValueChange.accept(v)
//?}
        }
    }
}