package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
//? if >=1.21 {
    /*//? if >=1.21.2 {
    /^private void onStartRender(com.mojang.blaze3d.resource.GraphicsResourceAllocator graphicsResourceAllocator, net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
     ^///?} else {
    private void onStartRender(net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, net.minecraft.client.renderer.LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
//?}
        LevelRenderCallback.EVENT.invoker().onStartRenderLevel(deltaTracker.getGameTimeDeltaPartialTick(true), camera, projectionMatrix, frustumMatrix.get3x3(new org.joml.Matrix3f()));
*///?} else {
    private void onStartRender(com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, net.minecraft.client.renderer.LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        LevelRenderCallback.EVENT.invoker().onStartRenderLevel(partialTick, camera, projectionMatrix, com.mojang.blaze3d.systems.RenderSystem.getInverseViewRotationMatrix().invert());
//?}
    }
}
