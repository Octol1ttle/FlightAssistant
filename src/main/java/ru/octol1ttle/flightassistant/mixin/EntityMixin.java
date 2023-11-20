package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setPitch", at = @At("HEAD"), cancellable = true)
    public void preventUpsetPitch(float pitch, CallbackInfo ci) {
        Entity that = (Entity) (Object) this;

        ComputerHost host = HudRenderer.getHost();
        if (that instanceof ClientPlayerEntity && host != null && host.data.canAutomationsActivate()) {
            boolean stalling = !host.faulted.contains(host.stall) && -pitch > host.stall.maximumSafePitch
                    || host.stall.anyStall();
            boolean highSinkRate = !host.faulted.contains(host.gpws) && !stalling && host.gpws.shouldBlockPitchChanges();
            boolean approachingVoidDamage = !host.faulted.contains(host.voidLevel) && -pitch < host.voidLevel.minimumSafePitch;
            if (stalling && pitch < that.getPitch() || (highSinkRate || approachingVoidDamage) && pitch > that.getPitch())
                ci.cancel();
        }
    }
}
