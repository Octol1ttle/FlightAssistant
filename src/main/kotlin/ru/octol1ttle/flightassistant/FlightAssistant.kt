package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import dev.architectury.event.events.client.ClientLifecycleEvent
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.profiling.ProfilerFiller
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback
import ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

//? if >=1.21.11 {
/*private val net.minecraft.client.Camera.xRot: Float
    get() = this.xRot()

private val net.minecraft.client.Camera.yRot: Float
    get() = this.yRot()
*///?}

object FlightAssistant {
    const val MOD_ID: String = "flightassistant"
    internal val mc: Minecraft = Minecraft.getInstance()
    internal val profiler: ProfilerFiller
//? if >=1.21.4 {
        /*get() = net.minecraft.util.profiling.Profiler.get()
*///?} else
        get() = mc.profiler
    internal val logger: Logger = LoggerFactory.getLogger("FlightAssistant")
    internal var initComplete: Boolean = false

    internal fun init() {
        logger.info("Initializing (stage 1)")
        FAConfig.load()
        FAKeyMappings.setup()
        ClientLifecycleEvent.CLIENT_STARTED.register {
            logger.info("Initializing (stage 2)")
            HudDisplayHost.sendRegistrationEvent(ComputerHost)
            ComputerHost.sendRegistrationEvent()
            initComplete = true
        }
        LevelRenderCallback.EVENT.register { partialTick, cameraXRot, cameraYRot, projectionMatrix, frustumMatrix ->
            FAKeyMappings.checkPressed(ComputerHost)

            ComputerHost.tick(partialTick)

            RenderMatrices.projectionMatrix.set(projectionMatrix)
            RenderMatrices.worldSpaceMatrix.set(frustumMatrix)
//? if >=26.1 {
            /*RenderMatrices.modelViewMatrix.identity()
*///?} else
            RenderMatrices.modelViewMatrix.set(RenderSystem.getModelViewMatrix())

            RenderMatrices.worldSpaceNoRollMatrix.set(Matrix4f().apply {
                rotate(Axis.XP.rotationDegrees(cameraXRot))
                rotate(Axis.YP.rotationDegrees(cameraYRot + 180.0f))
            })

            RenderMatrices.ready = true
        }
        FixedGuiRenderCallback.EVENT.register { context, _ ->
            HudDisplayHost.render(context)
        }
    }

    internal fun id(path: String): ResourceLocation {
//? if >=1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
*///?} else
        return ResourceLocation(MOD_ID, path)
    }
}
