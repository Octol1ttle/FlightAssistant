package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.api.util.event.FireworkBoostCallback;

@Mixin(FireworkRocketEntity.class)
abstract class FireworkRocketEntityMixin {
    @Shadow
    private @Nullable LivingEntity attachedToEntity;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER))
    private void onFireworkActivation(CallbackInfo ci) {
        if (this.attachedToEntity instanceof LocalPlayer player) {
            FireworkBoostCallback.EVENT.invoker().onFireworkBoost((FireworkRocketEntity) (Object) this, player);
        }
    }
}
