package ru.octol1ttle.flightassistant.api.util

import net.minecraft.world.phys.Vec3
import org.jetbrains.annotations.Contract
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import ru.octol1ttle.flightassistant.FlightAssistant.mc

object ScreenSpace {
    private var viewport: IntArray = IntArray(4)

    internal fun updateViewport() {
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport)
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
            .project(
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
        return pos.z > -1 && pos.z < 1
    }

    fun getX(heading: Float, useNoRollMatrix: Boolean = true): Int? {
        val vec: Vector3f = fromWorldSpace(Vec3.directionFromRotation(0.0f, heading - 180.0f), useNoRollMatrix)
        if (!isVisible(vec)) {
            return null
        }

        return vec.x.toInt()
    }

    fun getY(pitch: Float, useNoRollMatrix: Boolean = true): Int? {
        val vec: Vector3f = fromWorldSpace(Vec3.directionFromRotation(-pitch, mc.entityRenderDispatcher.camera.yRot), useNoRollMatrix)
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
