package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.torocraft.flighthud.components.FlightStatusIndicator;

import static net.torocraft.flighthud.FlightSafetyMonitor.*;
import static net.torocraft.flighthud.HudComponent.CONFIG;
import static net.torocraft.flighthud.HudComponent.wrapHeading;

public class AutoFlightManager {
    public static final float FIREWORK_ACCELERATION_SPEED = 33.62f;

    public static boolean autoThrustEnabled = false;
    public static boolean autoPilotEnabled = false;

    public static Integer destinationX = null;
    public static Integer destinationZ = null;
    public static Integer targetAltitude = null;

    public static Float targetPitch = null;
    public static Float targetYaw = null;
    public static Float targetHeading = null;
    public static Double distanceToTarget = null;

    public static String statusString = "";

    public static void update(MinecraftClient mc, FlightComputer computer) {
        statusString = "";
        if (CONFIG == null || mc.player == null || !mc.player.isFallFlying() || mc.interactionManager == null)
            return;

        if (computer.speed > 30) thrustSet = true;
        if (autoThrustEnabled && usableFireworkHand != null) {
            if (gpwsLampColor == CONFIG.color && computer.pitch > 0 && (distanceToTarget == null || distanceToTarget > 25)) {
                if (thrustSet && (computer.speed < 25 || computer.velocityPerSecond.y < -8)) {
                    mc.interactionManager.interactItem(mc.player, usableFireworkHand);
                    lastFireworkActivationTimeMs = FlightStatusIndicator.currentTimeMs;
                    thrustSet = false;
                }
                statusString += "THR MCT";
            } else statusString += "THR IDLE";
        }

        if ((distanceToTarget == null || distanceToTarget > 25) && targetAltitude != null) {
            float pitchLimiter = (float) (Math.abs(targetAltitude - mc.player.getY()) + Math.max(computer.velocityPerSecond.horizontalLength(), FIREWORK_ACCELERATION_SPEED));
            targetPitch = (float) Math.max(-maximumSafePitch, Math.toDegrees(-Math.asin((targetAltitude - mc.player.getY()) / pitchLimiter)));
            statusString += "".equals(statusString) ? "ALT" : " | ALT";
        } else
            targetPitch = null;

        if (destinationX != null && destinationZ != null) {
            targetYaw = (float) Math.toDegrees(Math.atan2(-(destinationX - mc.player.getX()), destinationZ - mc.player.getZ()));
            targetHeading = (targetYaw + 180) % 360;
            distanceToTarget = Math.sqrt(mc.player.getPos().squaredDistanceTo(destinationX, mc.player.getY(), destinationZ));
            statusString += "".equals(statusString) ? "NAV" : " | NAV";
        } else {
            targetYaw = null;
            targetHeading = null;
            distanceToTarget = null;
        }

        if (autoPilotEnabled) {
            float deltaHeading = targetHeading != null ? wrapHeading(targetHeading) - wrapHeading(computer.heading) : 0.0f;
            if (deltaHeading < -180) {
                deltaHeading += 360;
            }

            changeLookDirection(mc.player, targetPitch != null ? (targetPitch + computer.pitch) * deltaTime : 0.0f,
                    deltaHeading * deltaTime * 1.25f);
            statusString += "".equals(statusString) ? "AP" : " | AP";
        }
    }

    public static void changeLookDirection(PlayerEntity player, float pitch, float yaw) {
        player.setPitch(MathHelper.clamp(player.getPitch() + pitch, -90.0F, 90.0F));
        player.setYaw(player.getYaw() + yaw);
        player.prevPitch = MathHelper.clamp(player.prevPitch + pitch, -90.0F, 90.0F);
        player.prevYaw += yaw;
    }
}
