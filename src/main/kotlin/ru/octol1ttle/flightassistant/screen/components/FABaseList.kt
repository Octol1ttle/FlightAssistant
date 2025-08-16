package ru.octol1ttle.flightassistant.screen.components

import net.minecraft.client.gui.components.ContainerObjectSelectionList
import ru.octol1ttle.flightassistant.FlightAssistant.mc

abstract class FABaseList<E : ContainerObjectSelectionList.Entry<E>>
    (y0: Int, y1: Int, width: Int, itemHeight: Int) : ContainerObjectSelectionList<E>(mc, width, y1 - y0, y0, /*? if <1.21 {*/ y1, /*?}*/ itemHeight) {
    init {
//? if <1.21 {
        setRenderBackground(false)
        setRenderTopAndBottom(false)
//?}
    }

//? if >=1.21.4 {
    /*override fun scrollBarX(): Int {
*///?} else
    override fun getScrollbarPosition(): Int {
        return this.width - 6
    }

    override fun getRowWidth(): Int {
        return this.width
    }
}