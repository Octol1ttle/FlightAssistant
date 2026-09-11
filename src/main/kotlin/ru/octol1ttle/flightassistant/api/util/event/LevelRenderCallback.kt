package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import org.joml.Matrix3f
import org.joml.Matrix4fc

fun interface LevelRenderCallback {
    /**
     * Called when the LevelRenderer starts rendering the level
     */
    fun onStartRenderLevel(partialTick: Float, cameraXRot: Float, cameraYRot: Float, projectionMatrix: Matrix4fc, frustumMatrix: Matrix3f)

    companion object {
        @JvmField
        val EVENT: Event<LevelRenderCallback> = EventFactory.createLoop()
    }
}
