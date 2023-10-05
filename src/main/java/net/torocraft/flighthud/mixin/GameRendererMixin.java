package net.torocraft.flighthud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.HudRenderer;
import org.joml.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderWorld(float tickDelta,
                             long limitTime,
                             MatrixStack matrices,
                             CallbackInfo ci
    ) {
        Matrix3f inverseViewRotationMatrix = RenderSystem.getInverseViewRotationMatrix();
        if (HudRenderer.INSTANCE != null) {
            HudRenderer.INSTANCE.computer.updateRoll(inverseViewRotationMatrix);
        }
    }
}
