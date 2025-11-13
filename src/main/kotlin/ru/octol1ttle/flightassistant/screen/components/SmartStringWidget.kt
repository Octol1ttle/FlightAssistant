package ru.octol1ttle.flightassistant.screen.components

import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class SmartStringWidget(x: Int, y: Int, component: Component) : StringWidget(x, y, font.width(component), font.lineHeight, component, font) {
    init {
        this.leftAligned()
    }

    fun leftAligned(): SmartStringWidget {
//? if <1.21.9 {
        this.alignLeft()
//?}
        return this
    }

    fun middleAligned(): SmartStringWidget {
        this.x -= this.width / 2
        return this
    }

    fun rightAligned(): SmartStringWidget {
        this.x -= this.width
        return this
    }
}