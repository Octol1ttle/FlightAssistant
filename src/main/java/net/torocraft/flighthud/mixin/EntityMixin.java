package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.torocraft.flighthud.HudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setPitch", at = @At("HEAD"), cancellable = true)
    public void preventUpsetPitch(float pitch, CallbackInfo ci) {
        Entity that = (Entity) (Object) this;

        if (that instanceof ClientPlayerEntity cpe && cpe.isFallFlying() && HudRenderer.getComputer() != null) {
            boolean highSinkRate = pitch > that.getPitch()
                    && HudRenderer.getComputer().gpws.shouldBlockPitchChanges();
            if (highSinkRate)
                ci.cancel();
        }
    }
}
