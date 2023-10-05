package net.torocraft.flighthud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.torocraft.flighthud.HudRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (HudRenderer.INSTANCE != null) {
            HudRenderer.INSTANCE.render(context, client);
            HudRenderer.INSTANCE.computer.tickPitchController(tickDelta);
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (HudRenderer.INSTANCE == null) {
            HudRenderer.INSTANCE = new HudRenderer(client);
        }
        HudRenderer.INSTANCE.computer.tick();
    }
}
