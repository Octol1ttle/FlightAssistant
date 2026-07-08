package ru.octol1ttle.flightassistant.impl.computer.safety

import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus

// TODO: move min/max pitch here
// TODO: honestly could move *all* protections here
// TODO: ...or get rid of this computer entirely?
class FlightProtectionsComputer(computers: ComputerBus) : Computer(computers) {
    var protectionsLost: Boolean = false

    override fun tick() {
        // HACK
        if (protectionsLost || this.isDisabledOrFaulted() || computers.data.isDisabledOrFaulted() || computers.gpws.isDisabledOrFaulted() || computers.pitch.isDisabledOrFaulted()) {
            throw Exception("A safety-critical flight computer has faulted")
        }
    }

    override fun reset() {
        protectionsLost = this.isDisabledOrFaulted()
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_protections")
    }
}
