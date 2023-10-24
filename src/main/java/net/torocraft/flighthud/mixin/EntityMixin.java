package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.torocraft.flighthud.HudRenderer;
import net.torocraft.flighthud.computers.FlightComputer;
import net.torocraft.flighthud.computers.StallComputer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setPitch", at = @At("HEAD"), cancellable = true)
    public void preventUpsetPitch(float pitch, CallbackInfo ci) {
        Entity that = (Entity) (Object) this;

        FlightComputer computer = HudRenderer.getComputer();
        if (that instanceof ClientPlayerEntity && computer != null && computer.shouldUpdatePitch()) {
            boolean stalling = -pitch > computer.stall.maximumSafePitch
                    || computer.stall.stalling >= StallComputer.STATUS_APPROACHING_STALL;
            boolean highSinkRate = !stalling && computer.gpws.shouldBlockPitchChanges();
            boolean approachingVoidDamage = -pitch < computer.voidDamage.minimumSafePitch;
            if (stalling && pitch < that.getPitch() || (highSinkRate || approachingVoidDamage) && pitch > that.getPitch())
                ci.cancel();
        }
    }
}
