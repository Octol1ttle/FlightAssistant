package ru.octol1ttle.flightassistant

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import dev.architectury.event.events.client.ClientLifecycleEvent
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.octol1ttle.flightassistant.api.util.RenderMatrices
import ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback
import ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

object FlightAssistant {
    const val MOD_ID: String = "flightassistant"
    internal val mc: Minecraft = Minecraft.getInstance()
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
        LevelRenderCallback.EVENT.register { partialTick, camera, projectionMatrix, frustumMatrix ->
            FAKeyMappings.checkPressed(ComputerHost)

            ComputerHost.tick(partialTick)

            RenderMatrices.projectionMatrix.set(projectionMatrix)
            RenderMatrices.worldSpaceMatrix.set(frustumMatrix)
            RenderMatrices.modelViewMatrix.set(RenderSystem.getModelViewMatrix())

            RenderMatrices.worldSpaceNoRollMatrix.set(Matrix4f().apply {
                rotate(Axis.XP.rotationDegrees(camera.xRot))
                rotate(Axis.YP.rotationDegrees(camera.yRot + 180.0f))
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
