package ru.octol1ttle.flightassistant.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;

//? if fabric && >=1.21.6 {
//? if <26.2 {
@Mixin(net.minecraft.client.gui.Gui.class)
abstract class GuiMixinNew {
    @org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void beforeRenderHotbarAndDecorations(net.minecraft.client.gui.GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        guiGraphics.nextStratum();
    }
}
//?} else {
@Mixin(net.minecraft.client.gui.Hud.class)
abstract class GuiMixinNew {
    @org.spongepowered.asm.mixin.injection.Inject(method = "extractRenderState", at = @org.spongepowered.asm.mixin.injection.At("TAIL"))
    private void afterExtractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
    }
}
//?}
//?}
