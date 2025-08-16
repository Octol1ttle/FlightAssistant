package ru.octol1ttle.flightassistant.mixin;

import dev.architectury.platform.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.MixinHandlerKt;
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput;
import ru.octol1ttle.flightassistant.api.util.event.EntityTurnEvents;

@Mixin(Entity.class)
abstract class EntityTurnMixin {
    @ModifyVariable(method = "turn", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        List<ControlInput> list = new ArrayList<>();
        EntityTurnEvents.X_ROT.invoker().onEntityTurn(pitchDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangePitch(list), pitchDelta);
    }

    @ModifyVariable(method = "turn", at = @At("STORE"), ordinal = 1)
    private float overrideHeadingChange(float headingDelta) {
        if (Platform.isModLoaded("do_a_barrel_roll") && Math.abs(headingDelta) >= 360.0f) {
            headingDelta %= 360.0f;
        }
        List<ControlInput> list = new ArrayList<>();
        EntityTurnEvents.Y_ROT.invoker().onEntityTurn(headingDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangeHeading(list), headingDelta);
    }
}
