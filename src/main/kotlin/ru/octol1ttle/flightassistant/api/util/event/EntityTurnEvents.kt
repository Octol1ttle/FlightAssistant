package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

class EntityTurnEvents private constructor() {
    companion object {
        @JvmField
        val X_ROT: Event<EntityTurn> = EventFactory.createLoop()
        @JvmField
        val Y_ROT: Event<EntityTurn> = EventFactory.createLoop()
    }

    fun interface EntityTurn {
        fun onEntityTurn(delta: Float, output: MutableList<ControlInput>)
    }
}
