package net.torocraft.flighthud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.torocraft.flighthud.HudRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lnet/minecraft/util/math/Matrix3f;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderWorld(float tickDelta,
                             long limitTime,
                             MatrixStack matrices,
                             CallbackInfo ci
    ) {
        Matrix3f inverseViewRotationMatrix = RenderSystem.getInverseViewRotationMatrix();
        inverseViewRotationMatrix.invert();
        HudRenderer.INSTANCE.computer.update(client, inverseViewRotationMatrix);
    }
}
