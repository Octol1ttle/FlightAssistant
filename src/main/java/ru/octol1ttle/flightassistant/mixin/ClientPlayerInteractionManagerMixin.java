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
import ru.octol1ttle.flightassistant.computers.FlightComputer;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    public void disallowUnsafeFireworks(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        FlightComputer computer = HudRenderer.getComputer();
        if (!player.isFallFlying() || !(stack.getItem() instanceof FireworkRocketItem) || computer == null) {
            if (computer != null) {
                computer.firework.unsafeFireworks = false;
            }
            return;
        }


        if (!computer.firework.isFireworkSafe(stack)) {
            computer.firework.unsafeFireworks = true;
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
