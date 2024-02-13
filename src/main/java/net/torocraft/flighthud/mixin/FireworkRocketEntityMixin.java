package net.torocraft.flighthud.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.torocraft.flighthud.FlightSafetyMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    public void onFireworkActivation(CallbackInfo ci) {
        FlightSafetyMonitor.thrustSet = true;
    }
}