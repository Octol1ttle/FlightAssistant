package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.torocraft.flighthud.FlightSafetyMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setPitch", at = @At("HEAD"), cancellable = true)
    public void preventUpsetPitch(float pitch, CallbackInfo ci) {
        Entity that = (Entity) (Object) this;

        if (that instanceof ClientPlayerEntity cpe && cpe.isFallFlying() && FlightSafetyMonitor.flightProtectionsEnabled) {
            boolean approachingStall = pitch < that.getPitch() && pitch < -FlightSafetyMonitor.maximumSafePitch;
            boolean highSinkRate = pitch > that.getPitch() && FlightSafetyMonitor.secondsUntilGroundImpact <= 5.0f;
            if (approachingStall || highSinkRate)
                ci.cancel();
        }
    }
}
