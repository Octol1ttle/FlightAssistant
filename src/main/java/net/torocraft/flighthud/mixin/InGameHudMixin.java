package net.torocraft.flighthud.mixin;

import net.fabricmc.loader.api.FabricLoader;
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

  @Inject(method = "render", at = @At("RETURN"))
  private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
    if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
      net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.beginHudBatching();
    HudRenderer.INSTANCE.render(context, client);
    if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
      net.torocraft.flighthud.compat.ImmediatelyFastBatchingAccessor.endHudBatching();
  }
}
