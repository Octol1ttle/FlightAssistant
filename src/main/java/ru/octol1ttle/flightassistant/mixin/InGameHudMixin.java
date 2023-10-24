package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.HudRenderer;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (HudRenderer.getComputer() != null) {
            HudRenderer.INSTANCE.render(context, client);
            HudRenderer.getComputer().onRender();
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (client.player == null) {
            return;
        }
        if (HudRenderer.getComputer() == null) {
            HudRenderer.INSTANCE = new HudRenderer(client);
        }
        HudRenderer.getComputer().tick();
    }
}
