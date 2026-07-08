package ru.octol1ttle.flightassistant.mixin.level_renderer;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixinNewestest {
//? if >=26.2 {
    @org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onStartRender(com.mojang.blaze3d.resource.GraphicsResourceAllocator graphicsResourceAllocator, net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, net.minecraft.client.renderer.state.level.CameraRenderState cameraState, org.joml.Matrix4fc frustumMatrix, com.mojang.blaze3d.buffers.GpuBufferSlice fogBuffer, org.joml.Vector4f fogColor, boolean renderSky, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback.EVENT.invoker().onStartRenderLevel(deltaTracker.getGameTimeDeltaPartialTick(true), net.minecraft.client.Minecraft.getInstance().gameRenderer.mainCamera(), cameraState.projectionMatrix, frustumMatrix.get3x3(new org.joml.Matrix3f()));
    }
//?} else if >=1.21.9 {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "renderLevel", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onStartRender(com.mojang.blaze3d.resource.GraphicsResourceAllocator graphicsResourceAllocator, net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, net.minecraft.client.Camera camera, org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix, org.joml.Matrix4f cullingProjectionMatrix, com.mojang.blaze3d.buffers.GpuBufferSlice fogBuffer, org.joml.Vector4f fogColor, boolean renderSky, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback.EVENT.invoker().onStartRenderLevel(deltaTracker.getGameTimeDeltaPartialTick(true), camera, projectionMatrix, frustumMatrix.get3x3(new org.joml.Matrix3f()));
    }
*///?}
}
