package ru.octol1ttle.flightassistant

//? if fabric {
import dev.architectury.registry.client.keymappings.KeyMappingRegistry
import net.fabricmc.api.ClientModInitializer
import nl.enjarai.doabarrelroll.compat.flightassistant.DaBRCompatFA

object FlightAssistantFabric : ClientModInitializer {
    override fun onInitializeClient() {
        FlightAssistant.init()
        DaBRCompatFA.init()
        FAKeyMappings.keyMappings.forEach(KeyMappingRegistry::register)
    }
}
//?}
