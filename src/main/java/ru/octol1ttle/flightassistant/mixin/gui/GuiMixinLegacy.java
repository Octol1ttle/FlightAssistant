package ru.octol1ttle.flightassistant.mixin.gui;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
abstract class GuiMixinLegacy {
//? if fabric && <1.21 {
    @org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;", ordinal = 0))
    private void render(net.minecraft.client.gui.GuiGraphics guiGraphics, float partialTick, org.spongepowered.asm.mixin.injection.callback.CallbackInfo callbackInfo) {
        ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback.EVENT.invoker().onRenderGui(guiGraphics, partialTick);
    }
//?}
}
