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
        if (!host.data.isFlying || !(stack.getItem() instanceof FireworkRocketItem)) {
            host.firework.unsafeFireworks = false;
            return;
        }

        if (!host.faulted.contains(host.autoflight) && host.autoflight.autoFireworkEnabled && !host.firework.activationInProgress) {
            host.autoflight.disconnectAutoFirework(true);
        }

        host.firework.unsafeFireworks = !host.firework.isFireworkSafe(stack);

        boolean gpwsDanger = !host.faulted.contains(host.gpws) && host.gpws.isInDanger();
        boolean wallCollision = !host.faulted.contains(host.collision) && host.collision.collided;
        if (host.firework.unsafeFireworks || gpwsDanger || wallCollision) {
            cir.setReturnValue(ActionResult.FAIL);
            return;
        }

        if (host.firework.fireworkResponded) {
            if (!host.faulted.contains(host.time) && host.time.prevMillis != null) {
                host.firework.lastUseTime = host.time.prevMillis;
            }
            host.firework.fireworkResponded = false;
        }
    }
}
