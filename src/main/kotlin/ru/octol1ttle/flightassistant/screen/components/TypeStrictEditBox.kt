package ru.octol1ttle.flightassistant.screen.components

import com.google.common.base.Predicates
import java.util.function.Consumer
import java.util.function.Predicate
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TypeStrictEditBox<T>(x: Int, y: Int, width: Int, height: Int, initialValue: T, onValueChange: Consumer<T>, private val convertFunction: (String) -> T?, private val filter: Predicate<T> = Predicates.alwaysTrue<T>()) : EditBox(font, x, y, width, height, Component.empty()) {
    init {
        this.value = initialValue.toString()
//? if <26.1 {
        this.setFilter {
            val value: T? = convertFunction.invoke(it)
            value != null && filter.test(value)
        }
//?}
        this.setResponder { onValueChange.accept(convertFunction.invoke(it)!!) }
    }

//? if >=26.1 {
    /*private fun isValid(candidate: String): Boolean {
        val value: T? = convertFunction.invoke(candidate)
        return value != null && filter.test(value)
    }

    override fun insertText(text: String) {
        val previousValue: String = this.value
        val previousCursor: Int = this.cursorPosition
        super.insertText(text)
        if (!isValid(this.value)) {
            this.value = previousValue
            this.setCursorPosition(previousCursor)
        }
    }
*///?}
}