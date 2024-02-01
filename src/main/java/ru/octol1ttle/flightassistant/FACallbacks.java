package ru.octol1ttle.flightassistant;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import org.joml.Matrix3f;
import ru.octol1ttle.flightassistant.computers.ComputerHost;

public class FACallbacks {
    public static void setup() {
        setupWorldRender();
        setupHudRender();
        setupUseItem();
    }

    private static void setupWorldRender() {
        WorldRenderEvents.END.register(context -> {
            ComputerHost host = HudRenderer.getHost();
            if (host != null && !host.faulted.contains(host.data)) {
                Matrix3f inverseViewRotationMatrix = RenderSystem.getInverseViewRotationMatrix();
                host.data.updateRoll(inverseViewRotationMatrix.invert());
            }
        });
    }

    private static void setupHudRender() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) {
                return;
            }

            if (HudRenderer.getHost() == null) {
                HudRenderer.INSTANCE = new HudRenderer(client);
            }
            HudRenderer.getHost().tick();

            HudRenderer.INSTANCE.render(drawContext, client);
        });
    }

    private static void setupUseItem() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            ComputerHost host = HudRenderer.getHost();
            if (!world.isClient() || host == null || host.faulted.contains(host.firework)) {
                return TypedActionResult.pass(stack);
            }
            if (!host.data.isFlying || !(stack.getItem() instanceof FireworkRocketItem)) {
                host.firework.unsafeFireworks = false;
                return TypedActionResult.pass(stack);
            }

            if (!host.faulted.contains(host.autoflight) && host.autoflight.autoFireworkEnabled && !host.firework.activationInProgress) {
                host.autoflight.disconnectAutoFirework(true);
            }

            host.firework.unsafeFireworks = !host.firework.isFireworkSafe(stack);

            boolean gpwsDanger = !host.faulted.contains(host.gpws) && host.gpws.isInDanger();
            boolean wallCollision = !host.faulted.contains(host.collision) && host.collision.collided;
            if (!host.firework.activationInProgress && (host.firework.unsafeFireworks || gpwsDanger || wallCollision)) {
                return TypedActionResult.fail(stack);
            }

            if (host.firework.fireworkResponded) {
                if (!host.faulted.contains(host.time) && host.time.prevMillis != null) {
                    host.firework.lastUseTime = host.time.prevMillis;
                }
                host.firework.fireworkResponded = false;
            }

            return TypedActionResult.pass(stack);
        });
    }
}
