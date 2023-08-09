package net.torocraft.flighthud.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.torocraft.flighthud.FlightSafetyMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    public void detectWallCollision(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isOf(DamageTypes.FLY_INTO_WALL))
            FlightSafetyMonitor.thrustLocked = true;
    }
}
