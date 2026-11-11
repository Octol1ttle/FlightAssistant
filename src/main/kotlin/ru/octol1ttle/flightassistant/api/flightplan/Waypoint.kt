package ru.octol1ttle.flightassistant.api.flightplan

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.world.phys.Vec3
import java.util.UUID

@Serializable
data class Waypoint(val posX: Int = 0, val posZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, val phaseMarker: PhaseMarker = PhaseMarker.NONE, @Transient val uuid: UUID = UUID.randomUUID()) {
    fun vec3(): Vec3 {
        return Vec3(posX + 0.5, altitude.toDouble(), posZ + 0.5)
    }

    enum class PhaseMarker {
        NONE,
        TAKEOFF,
        APPROACH,
        LANDING
    }
}
