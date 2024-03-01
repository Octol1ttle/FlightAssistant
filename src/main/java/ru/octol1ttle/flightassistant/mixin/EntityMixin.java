package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    public float preventUpsetPitch(float pitchDelta) {
        Entity that = (Entity) (Object) this;

        ComputerHost host = HudRenderer.getHost();
        if (that instanceof ClientPlayerEntity && host != null && host.data.canAutomationsActivate()) {
            float oldPitch = host.data.pitch;
            float newPitch = oldPitch + (-pitchDelta);

            boolean stalling = !host.faulted.contains(host.stall) && host.stall.shouldBlockPitchChanges(newPitch);
            boolean gpwsDanger = !stalling && !host.faulted.contains(host.gpws) && host.gpws.shouldBlockPitchChanges();
            boolean approachingVoidDamage = !host.faulted.contains(host.voidLevel) && host.voidLevel.shouldBlockPitchChanges(newPitch);

            if (stalling && newPitch > oldPitch ||
                    (gpwsDanger || approachingVoidDamage) && newPitch < oldPitch) {
                return 0.0f;
            }
        }

        return pitchDelta;
    }
}
