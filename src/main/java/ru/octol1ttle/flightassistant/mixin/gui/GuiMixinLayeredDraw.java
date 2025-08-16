package ru.octol1ttle.flightassistant.mixin.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
abstract class GuiMixinLayeredDraw {
//? if fabric && >=1.21 && <1.21.6 {
    /*@com.llamalad7.mixinextras.injector.ModifyReceiver(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 2))
    public net.minecraft.client.gui.LayeredDraw render(net.minecraft.client.gui.LayeredDraw instance, net.minecraft.client.gui.LayeredDraw.Layer layer) {
        return instance.add((guiGraphics, deltaTracker)
                -> ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true))
        );
    }
*///?}
}
