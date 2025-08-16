package ru.octol1ttle.flightassistant.mixin.level_renderer;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixinOld {
//? if >=1.21 && <1.21.2 {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "renderLevel", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onStartRender(net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, net.minecraft.client.Camera camera, net.minecraft.client.renderer.GameRenderer gameRenderer, net.minecraft.client.renderer.LightTexture lightTexture, org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback.EVENT.invoker().onStartRenderLevel(deltaTracker.getGameTimeDeltaPartialTick(true), camera, projectionMatrix, frustumMatrix.get3x3(new org.joml.Matrix3f()));
    }
*///?}
}
