package net.torocraft.flighthud.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    public void disallowUnsafeFireworks(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        /*if (!player.isFallFlying()) return;

        if (FlightSafetyMonitor.flightProtectionsEnabled && FlightSafetyMonitor.unsafeFireworkHands.contains(hand))
            cir.setReturnValue(ActionResult.FAIL);
        else if (player.getStackInHand(hand).getItem() instanceof FireworkRocketItem) {
            if (FlightSafetyMonitor.flightProtectionsEnabled && !FlightSafetyMonitor.thrustSet
                    || FlightSafetyMonitor.thrustLocked)
                cir.setReturnValue(ActionResult.FAIL);
            else {
                FlightSafetyMonitor.lastFireworkActivationTimeMs = Util.getMeasuringTimeMs();
                FlightSafetyMonitor.thrustSet = false;
            }
        }*/
    }
}
