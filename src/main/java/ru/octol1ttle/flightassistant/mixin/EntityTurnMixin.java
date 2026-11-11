package ru.octol1ttle.flightassistant.mixin;

import dev.architectury.platform.Platform;

import java.util.Objects;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.MixinHandlerKt;
import ru.octol1ttle.flightassistant.api.game.AllowedManualPitchChangeQuery;
import ru.octol1ttle.flightassistant.api.game.AllowedManualYawChangeQuery;
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost;

@Mixin(Entity.class)
abstract class EntityTurnMixin {
    @ModifyVariable(method = "turn", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        var rightDelta = -pitchDelta;
        var results = ComputerHost.INSTANCE.dispatchQuery(new AllowedManualPitchChangeQuery(rightDelta));
        return -Objects.requireNonNullElse(MixinHandlerKt.onEntityChangePitch(results), rightDelta);
    }

    @ModifyVariable(method = "turn", at = @At("STORE"), ordinal = 1)
    private float overrideYawChange(float yawDelta) {
        if (Platform.isModLoaded("do_a_barrel_roll") && Math.abs(yawDelta) >= 360.0f) {
            yawDelta %= 360.0f;
        }
        var results = ComputerHost.INSTANCE.dispatchQuery(new AllowedManualYawChangeQuery(yawDelta));
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangeYaw(results), yawDelta);
    }
}
