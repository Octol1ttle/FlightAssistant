package ru.octol1ttle.flightassistant.api

import ru.octol1ttle.flightassistant.api.util.extensions.*
import net.minecraft.resources.ResourceLocation

interface ModuleView<T> {
    fun identifiers(): Collection<ResourceLocation>
    fun get(identifier: ResourceLocation): T
    fun isEnabled(identifier: ResourceLocation): Boolean
    fun isFaulted(identifier: ResourceLocation): Boolean
}
