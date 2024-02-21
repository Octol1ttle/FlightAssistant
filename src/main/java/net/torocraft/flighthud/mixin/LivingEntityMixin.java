package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.torocraft.flighthud.FlightSafetyMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onDamaged", at = @At("HEAD"))
    public void detectWallCollision(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity $this = (LivingEntity) (Object) this;
        if ($this instanceof ClientPlayerEntity && damageSource.isOf(DamageTypes.FLY_INTO_WALL)) {
            FlightSafetyMonitor.thrustLocked = true;
        }
    }
}
