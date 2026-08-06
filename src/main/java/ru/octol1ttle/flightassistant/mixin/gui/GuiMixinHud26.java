package ru.octol1ttle.flightassistant.mixin.gui;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Hud.class)
abstract class GuiMixinHud26 {
//? if >=26 {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "extractRenderState", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void beforeExtractHotbarAndDecorations(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        guiGraphics.nextStratum();
    }
*///?}
}
