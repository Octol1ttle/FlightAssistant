package net.torocraft.flighthud;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

import static net.torocraft.flighthud.AutoFlightManager.changeLookDirection;
import static net.torocraft.flighthud.AutoFlightManager.deltaTime;
import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.FlightHud.LOGGER;
import static net.torocraft.flighthud.HudComponent.CONFIG;

public class FlightSafetyMonitor {
    public static List<Hand> unsafeFireworkHands = new ObjectArrayList<>(2);
    public static Hand usableFireworkHand = null;

    public static boolean isElytraLow = false;

    public static boolean isStalling = false;
    public static float maximumSafePitch = 0.0f;

    public static float secondsUntilGroundImpact = 0.0f;
    public static float secondsUntilTerrainImpact = 0.0f;
    public static float terrainDetectionTimer = 0.0f;
    public static int gpwsLampColor;

    public static float lampThreshold = 0.0f;
    public static float cautionThreshold = 0.0f;
    public static float warningThreshold = 0.0f;
    public static float correctThreshold = 0.0f;

    public static boolean flightProtectionsEnabled = true;

    public static boolean thrustSet = true;
    public static long lastFireworkActivationTimeMs = 0;

    public static int fireworkCount = Integer.MAX_VALUE;
    public static boolean thrustLocked = false;

    public static void update(MinecraftClient mc, FlightComputer computer) {
        if (CONFIG == null || mc.world == null || mc.player == null || !mc.player.isFallFlying()) {
            flightProtectionsEnabled = thrustSet = true;
            thrustLocked = false;
            return;
        }
        maximumSafePitch = updateMaximumSafePitch(computer);
        secondsUntilGroundImpact = updateUnsafeSinkrate(computer);

        boolean hasCeiling = mc.player.getWorld().getDimension().hasCeiling();
        lampThreshold = hasCeiling ? 7.5f : 10.0f;
        cautionThreshold = hasCeiling ? 5.0f : 7.5f;
        warningThreshold = hasCeiling ? 2.5f : 5.0f;
        correctThreshold = hasCeiling ? 1.0f : 2.5f;

        if (secondsUntilGroundImpact <= warningThreshold || secondsUntilTerrainImpact <= warningThreshold)
            gpwsLampColor = CONFIG.alertColor;
        else if (secondsUntilGroundImpact <= lampThreshold || secondsUntilTerrainImpact <= lampThreshold)
            gpwsLampColor = CONFIG.amberColor;
        else
            gpwsLampColor = CONFIG.color;

        isStalling = updateStallStatus(computer);
        isElytraLow = updateElytraLow(computer);
        secondsUntilTerrainImpact = updateUnsafeTerrainClearance(mc.player, computer);
        boolean wereFireworksSafe = unsafeFireworkHands.isEmpty();
        updateUnsafeFireworks(mc.player);
        if (wereFireworksSafe && !unsafeFireworkHands.isEmpty())
            LOGGER.error("Unsafe fireworks in {}", unsafeFireworkHands.get(0).toString().replace('_', ' '));
        fireworkCount = countSafeFireworks(mc.player);

        if (flightProtectionsEnabled) { // Make corrections to flight path to ensure safety
            if (computer.distanceFromGround > 2 && computer.pitch > maximumSafePitch)
                changeLookDirection(mc.player, Math.max(0, computer.pitch - maximumSafePitch) * AutoFlightManager.deltaTime, 0);

            else if (secondsUntilGroundImpact <= correctThreshold)
                changeLookDirection(mc.player, Math.min(0, computer.pitch) * (AutoFlightManager.deltaTime / Math.min(1, secondsUntilGroundImpact)), 0);
        }
    }

    private static void updateUnsafeFireworks(PlayerEntity player) {
        unsafeFireworkHands.clear();
        if (!CONFIG_SETTINGS.unsafeFireworksAlert) return;

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();

        usableFireworkHand = null;
        if (off.getItem() instanceof FireworkRocketItem) {
            NbtCompound nbtCompound = off.getSubNbt("Fireworks");
            if (nbtCompound != null && !nbtCompound.getList("Explosions", 10).isEmpty())
                unsafeFireworkHands.add(Hand.OFF_HAND);
            else usableFireworkHand = Hand.OFF_HAND;
        }
        if (main.getItem() instanceof FireworkRocketItem) {
            NbtCompound nbtCompound = main.getSubNbt("Fireworks");
            if (nbtCompound != null && !nbtCompound.getList("Explosions", 10).isEmpty())
                unsafeFireworkHands.add(Hand.MAIN_HAND);
            else usableFireworkHand = Hand.MAIN_HAND;
        }
    }

    private static boolean updateElytraLow(FlightComputer computer) {
        final boolean b = CONFIG_SETTINGS.lowElytraHealthAlarm && computer.elytraHealth != null && computer.elytraHealth <= CONFIG_SETTINGS.lowElytraHealthAlarmThreshold;
        if (b && !isElytraLow)
            LOGGER.error("Elytra health low: {}; threshold {}", computer.elytraHealth, CONFIG_SETTINGS.lowElytraHealthAlarmThreshold);

        return b;
    }

    private static boolean updateStallStatus(FlightComputer computer) {
        final boolean b = computer.pitch > 0 && computer.distanceFromGround > 2 && computer.velocity.horizontalLength() < -computer.velocity.y;
        if (b && !isStalling)
            LOGGER.error("Stall: pitch {}; radar altitude {}; G/S {}; V/S {}", computer.pitch, computer.distanceFromGround, computer.velocityPerSecond.horizontalLength(), computer.velocityPerSecond.y);

        return b;
    }

    private static float updateMaximumSafePitch(FlightComputer computer) {
        return isStalling && computer.velocityPerSecond.y <= -9 ? 0.0f : computer.speed * 3;
    }

    private static float updateUnsafeSinkrate(FlightComputer computer) {
        if (!CONFIG_SETTINGS.gpws || isStalling || computer.distanceFromGround <= 2 || computer.velocityPerSecond.y > -10)
            return Float.MAX_VALUE;
        float f = (float) (computer.distanceFromGround / -computer.velocityPerSecond.y);
        if (f <= 10.0f && secondsUntilGroundImpact > 10.0f)
            LOGGER.error("Excessive sink rate: radar altitude {}; V/S {}; seconds until impact {}",
                    computer.distanceFromGround, computer.velocityPerSecond.y, f);

        return f;
    }

    private static float updateUnsafeTerrainClearance(PlayerEntity player, FlightComputer computer) {
        if (!CONFIG_SETTINGS.gpws || isStalling || computer.velocityPerSecond.horizontalLength() <= 15)
            return Float.MAX_VALUE;
        Vec3d vec = raycast(player, computer, 10);
        float f = vec == null ? Float.MAX_VALUE : (float) (vec.subtract(player.getPos()).horizontalLength() / computer.velocityPerSecond.horizontalLength());
        if (f <= 10.0f) {
            if (f <= 5.0f && secondsUntilTerrainImpact > 5.0f)
                LOGGER.error("Unsafe terrain clearance: terrain XZ: {} {}; G/S {}; seconds until impact {}",
                        vec.x, vec.z, computer.velocityPerSecond.horizontalLength(), f);
            f = Math.min(f, secondsUntilTerrainImpact);
            terrainDetectionTimer = Math.min(0.5f, terrainDetectionTimer + deltaTime);
        } else {
            terrainDetectionTimer = Math.max(0.0f, terrainDetectionTimer - deltaTime);
            if (terrainDetectionTimer > 0.0f)
                f = Math.min(f, secondsUntilTerrainImpact);
        }

        return f > 10.0f || terrainDetectionTimer >= Math.min(0.5f, f * 0.2f) ? f : Float.MAX_VALUE;
    }

    private static int countSafeFireworks(PlayerEntity player) {
        int i = 0;

        for (int j = 0; j < player.getInventory().size(); j++) {
            ItemStack stack = player.getInventory().getStack(j);
            if (stack.getItem().equals(Items.FIREWORK_ROCKET)) {
                NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
                if (nbtCompound == null || nbtCompound.getList("Explosions", 10).isEmpty())
                    i += stack.getCount();
            }
        }

        return i;
    }

    public static Vec3d raycast(PlayerEntity player, FlightComputer computer, int seconds) {
        Vec3d vel = computer.velocityPerSecond;
        Vec3d end = player.getPos().add(vel.multiply(seconds));

        BlockHitResult result = player.getWorld().raycast(new RaycastContext(player.getPos(), end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
        if (result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP || result.getSide() == Direction.DOWN)
            return null;
        return result.getPos();
    }
}
