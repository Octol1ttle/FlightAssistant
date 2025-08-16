package ru.octol1ttle.flightassistant.mixin.level_renderer;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixinLegacy {
//? if <1.21 {
    @org.spongepowered.asm.mixin.injection.Inject(method = "renderLevel", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onStartRender(com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, net.minecraft.client.Camera camera, net.minecraft.client.renderer.GameRenderer gameRenderer, net.minecraft.client.renderer.LightTexture lightTexture, org.joml.Matrix4f projectionMatrix, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback.EVENT.invoker().onStartRenderLevel(partialTick, camera, projectionMatrix, com.mojang.blaze3d.systems.RenderSystem.getInverseViewRotationMatrix().invert());
    }
//?}
}
