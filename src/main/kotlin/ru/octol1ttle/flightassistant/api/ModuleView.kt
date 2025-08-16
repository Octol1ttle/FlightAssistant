package ru.octol1ttle.flightassistant.api

import net.minecraft.resources.ResourceLocation

interface ModuleView<T> {
    fun identifiers(): Collection<ResourceLocation>
    fun get(identifier: ResourceLocation): T
    fun isEnabled(identifier: ResourceLocation): Boolean
    fun isFaulted(identifier: ResourceLocation): Boolean
}
