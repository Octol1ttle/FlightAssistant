package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.client.Camera
import org.joml.Matrix3f
import org.joml.Matrix4f

fun interface LevelRenderCallback {
    /**
     * Called when the LevelRenderer starts rendering the level
     */
    fun onStartRenderLevel(partialTick: Float, camera: Camera, projectionMatrix: Matrix4f, frustumMatrix: Matrix3f)

    companion object {
        @JvmField
        val EVENT: Event<LevelRenderCallback> = EventFactory.createLoop()
    }
}
