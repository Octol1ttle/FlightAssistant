package ru.octol1ttle.flightassistant.mixin.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
abstract class GuiMixinNew {
//? if fabric && >=1.21.6 {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void beforeRenderHotbarAndDecorations(net.minecraft.client.gui.GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker deltaTracker, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true));
        guiGraphics.nextStratum();
    }
*///?}
}
