package ru.octol1ttle.flightassistant.screen.components

import com.google.common.base.Predicates
import java.util.function.Consumer
import java.util.function.Predicate
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TypeStrictEditBox<T>(x: Int, y: Int, width: Int, height: Int, initialValue: T, onValueChange: Consumer<T>, val convertFunction: (String) -> T?, val filter: Predicate<T> = Predicates.alwaysTrue<T>()) : EditBox(font, x, y, width, height, Component.empty()) {
    init {
        this.value = initialValue.toString()
//? if <26 {
        this.setFilter {
            val value: T? = convertFunction.invoke(it)
            value != null && filter.test(value)
        }
//?}
        this.setResponder { onValueChange.accept(convertFunction.invoke(it)!!) }
    }

//? if >=26 {
    /*// 26.x removed EditBox.setFilter; guard the mutation methods instead so the
    // value can never become invalid (the responder relies on that with `!!`).
    override fun insertText(text: String) {
        applyIfValid { super.insertText(text) }
    }

    override fun deleteChars(i: Int) {
        applyIfValid { super.deleteChars(i) }
    }

    override fun deleteWords(i: Int) {
        applyIfValid { super.deleteWords(i) }
    }

    private fun applyIfValid(change: () -> Unit) {
        val original: String = this.value
        change()
        val parsed: T? = convertFunction.invoke(this.value)
        if (parsed == null || !filter.test(parsed)) {
            this.value = original
        }
    }
*///?}
}