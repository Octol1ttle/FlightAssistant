package ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin

import kotlin.math.atan2
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class HeadingLateralMode(val target: Int) : AutoFlightComputer.LateralMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            target.toFloat(),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.lateral.heading")
        )
    }
}

data class DirectCoordinatesLateralMode(val targetX: Int, val targetZ: Int) : AutoFlightComputer.LateralMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            degrees(atan2(-(targetX - computers.data.position.x), targetZ - computers.data.position.z)).toFloat() + 180.0f,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.lateral.direct_coordinates")
        )
    }
}

data class TrackNavigationLateralMode(val originX: Int, val originZ: Int, val targetX: Int, val targetZ: Int) : AutoFlightComputer.LateralMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        TODO()
    }
}