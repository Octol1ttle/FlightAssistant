package ru.octol1ttle.flightassistant.api.flightplan.legacy

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.world.phys.Vec3
import java.util.UUID

@Serializable
@Deprecated("Use new flight plans instead. Only kept for backwards compatibility during loading")
data class EnrouteWaypoint(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, @Transient var active: Active? = null, @Transient val uuid: UUID = UUID.randomUUID()) {
    enum class Active {
        ORIGIN,
        TARGET
    }

    fun vec3(): Vec3 {
        return Vec3(coordinatesX.toDouble(), altitude.toDouble(), coordinatesZ.toDouble())
    }
}
