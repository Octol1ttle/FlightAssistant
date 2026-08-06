package ru.octol1ttle.flightassistant.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// The HUD class was renamed Gui -> Hud in 26.x, so this mixin targets a class that
// only exists on 26+. @Pseudo makes the mixin a no-op (skipped with a warning) on
// older versions where the target is absent, so it can stay registered in the config.
@Pseudo
@Mixin(targets = "net.minecraft.client.gui.Hud")
abstract class GuiMixinHud26 {
//? if >=26 {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "extractRenderState", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void beforeExtractHotbarAndDecorations(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        guiGraphics.nextStratum();
    }
*///?}
}
