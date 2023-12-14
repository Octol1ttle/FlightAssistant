package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    public void disallowUnsafeFireworks(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        ComputerHost host = HudRenderer.getHost();
        if (host == null || host.faulted.contains(host.firework)) {
            return;
        }
        if (!player.isFallFlying() || !(stack.getItem() instanceof FireworkRocketItem)) {
            host.firework.unsafeFireworks = false;
            return;
        }

        if (host.autoflight.autoFireworkEnabled && !host.firework.activationInProgress) {
            host.autoflight.disconnectAutoFirework(true);
            // TODO: announce the switch to "MAN FRWK" in F/MODE
            // TODO: announce "MAN FRWK" in F/MODE on initial firework usage
        }

        host.firework.unsafeFireworks = !host.firework.isFireworkSafe(stack);

        if (host.firework.unsafeFireworks) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
