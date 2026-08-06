package ru.octol1ttle.flightassistant.api.util

import net.minecraft.world.phys.Vec3
import org.jetbrains.annotations.Contract
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import ru.octol1ttle.flightassistant.FlightAssistant.mc

object ScreenSpace {
    private var viewport: IntArray = IntArray(4)

    internal fun updateViewport() {
        // The HUD is now extracted during the CPU render-state phase (26.x), where the
        // GL viewport is stale. Project against the actual framebuffer size instead.
        viewport[0] = 0
        viewport[1] = 0
        viewport[2] = mc.window.width
        viewport[3] = mc.window.height
    }

    /**
     * @author 0x150
     * @see <a href="https://github.com/0x3C50/Renderer">Original source code</a>
     */

    /**
     *
     * Transforms an input position into a (x, y, d) coordinate, transformed to screen space. d specifies the far plane of the position, and can be used to check if the position is on screen. Use [.isVisible].
     * Example:
     * <pre>
     * `// Hud render event
     * Vec3 targetPos = new Vec3(100, 64, 100); // world space
     * Vec3 screenSpace = ScreenSpaceRendering.fromWorldSpace(targetPos);
     * if (ScreenSpaceRendering.isVisible(screenSpace)) {
     * // do something with screenSpace.x and .y
     * }
    ` *
    </pre> *
     *
     * @param deltaPos The world space coordinates to translate, relative to the camera's current position
     * @return The (x, y, d) coordinates
     * @throws NullPointerException If `pos` is null
     */
    @Contract(value = "_ -> new", pure = true)
    private fun fromWorldSpace(deltaPos: Vec3, useNoRollMatrix: Boolean = true): Vector3f {
        val displayHeight: Int = mc.window.height
        val target = Vector3f()

        val transformedCoordinates: Vector4f =
            Vector4f(deltaPos.x.toFloat(), deltaPos.y.toFloat(), deltaPos.z.toFloat(), 1f).mul(
                if (useNoRollMatrix) RenderMatrices.worldSpaceNoRollMatrix else RenderMatrices.worldSpaceMatrix
            )

        val matrixProj = Matrix4f(RenderMatrices.projectionMatrix)
        val matrixModel = Matrix4f(RenderMatrices.modelViewMatrix)

        matrixProj.mul(matrixModel)

        // On 26.x the projection matrix uses a reversed-Z depth range. Points that are at
        // or behind the camera plane (clip.w <= 0) get mirrored back into the visible depth
        // window, so the generic z-range check below can no longer reject them. Reject them
        // here explicitly, otherwise e.g. both pitch-limit arrows end up drawn at once.
        val clipW: Float =
            matrixProj.m03() * transformedCoordinates.x +
                matrixProj.m13() * transformedCoordinates.y +
                matrixProj.m23() * transformedCoordinates.z +
                matrixProj.m33() * transformedCoordinates.w
        if (clipW <= 0.0f) {
            return Vector3f(-1.0f, -1.0f, 2.0f)
        }

        matrixProj.project(
            transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport,
            target
        )

        return Vector3f(
            target.x / mc.window.guiScale.toFloat(),
            (displayHeight - target.y) / mc.window.guiScale.toFloat(),
            target.z
        )
    }

    /**
     * Checks if a screen space coordinate (x, y, d) is on screen
     *
     * @param pos The (x, y, d) coordinates to check
     * @return True if the coordinates are visible
     */
    private fun isVisible(pos: Vector3f?): Boolean {
        if (pos == null) {
            return false
        }
        return pos.x >= 0 && pos.x <= mc.window.guiScaledWidth && pos.y >= 0 && pos.y <= mc.window.guiScaledHeight && pos.z > -1 && pos.z < 1
    }

    fun getX(heading: Float): Int? {
        val vec: Vector3f = fromWorldSpace(Vec3.directionFromRotation(0.0f, heading - 180.0f), true)
        if (!isVisible(vec)) {
            return null
        }

        return vec.x.toInt()
    }

    //? if >=1.21.11 {
    /*private val net.minecraft.client.Camera.yRot: Float
        get() = this.yRot()
    *///?}

    fun getY(pitch: Float): Int? {
        val vec: Vector3f = fromWorldSpace(Vec3.directionFromRotation(-pitch, mc.entityRenderDispatcher.camera!!.yRot), true)
        if (!isVisible(vec)) {
            return null
        }

        return vec.y.toInt()
    }

    fun getVector3f(deltaPos: Vec3, useNoRollMatrix: Boolean = true): Vector3f? {
        val vec: Vector3f = fromWorldSpace(deltaPos, useNoRollMatrix)
        if (!isVisible(vec)) {
            return null
        }

        return vec
    }
}
