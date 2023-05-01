package net.torocraft.flighthud.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
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
  private void render(MatrixStack ms, float partial, CallbackInfo info) {
    if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
      net.raphimc.immediatelyfast.feature.batching.BatchingBuffers.beginHudBatching();
    HudRenderer.INSTANCE.render(ms, partial, client);
    if (FabricLoader.getInstance().isModLoaded("immediatelyfast"))
      net.raphimc.immediatelyfast.feature.batching.BatchingBuffers.endHudBatching();
  }
}
