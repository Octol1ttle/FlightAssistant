package ru.octol1ttle.flightassistant.api.flightplan.legacy

import kotlinx.serialization.Serializable
import net.minecraft.world.phys.Vec3

@Serializable
@Deprecated("Use new flight plans instead. Only kept for backwards compatibility during loading")
data class ArrivalData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val landingThrust: Float = 0.0f, val minimums: Int = 0, val minimumsType: MinimumsType = MinimumsType.ABSOLUTE, val goAroundAltitude: Int = 0, val approachReEntryWaypointIndex: Int = 0) {
    enum class MinimumsType {
        ABSOLUTE,
        RELATIVE
    }

    fun isDefault(): Boolean {
        return this == DEFAULT
    }

    fun vec3(): Vec3 {
        return Vec3(coordinatesX.toDouble(), elevation.toDouble(), coordinatesZ.toDouble())
    }

    companion object {
        val DEFAULT: ArrivalData = ArrivalData()
    }
}
