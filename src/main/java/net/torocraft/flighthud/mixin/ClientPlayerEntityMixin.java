package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.torocraft.flighthud.FlightSafetyMonitor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    public void detectWallCollision(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity $this = (LivingEntity) (Object) this;
        if ($this.isFallFlying()
                // Damage source is always generic except fall damage in 1.19.2
                // Use bounding box to check for actual collision
                && (damageSource == DamageSource.FLY_INTO_WALL
                    || $this.getWorld().canCollide($this, $this.getBoundingBox().expand(3)))) {
            FlightSafetyMonitor.thrustLocked = true;
        }
    }
}
