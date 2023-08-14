package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import static net.torocraft.flighthud.FlightSafetyMonitor.correctThreshold;
import static net.torocraft.flighthud.FlightSafetyMonitor.gpwsLampColor;
import static net.torocraft.flighthud.FlightSafetyMonitor.lastFireworkActivationTimeMs;
import static net.torocraft.flighthud.FlightSafetyMonitor.maximumSafePitch;
import static net.torocraft.flighthud.FlightSafetyMonitor.secondsUntilTerrainImpact;
import static net.torocraft.flighthud.FlightSafetyMonitor.thrustLocked;
import static net.torocraft.flighthud.FlightSafetyMonitor.thrustSet;
import static net.torocraft.flighthud.FlightSafetyMonitor.usableFireworkHand;
import static net.torocraft.flighthud.HudComponent.CONFIG;
import static net.torocraft.flighthud.HudComponent.wrapHeading;

public class AutoFlightManager {
    public static Long lastUpdateTimeMs = null;
    public static Float deltaTime;

    public static boolean flightDirectorsEnabled = false;
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
        long currentTimeMs = Util.getMeasuringTimeMs();
        if (lastUpdateTimeMs != null)
            deltaTime = Math.min(1, (currentTimeMs - lastUpdateTimeMs) * 0.001f);
        else
            deltaTime = 1f / mc.options.getMaxFps().getValue();
        lastUpdateTimeMs = currentTimeMs;

        if (CONFIG == null || mc.player == null || !mc.player.isFallFlying() || mc.interactionManager == null)
            return;
        boolean approachingDestination = distanceToTarget != null && distanceToTarget < Math.max(40, computer.velocityPerSecond.horizontalLength());

        if (computer.speed > 30) thrustSet = true;

        if (autoThrustEnabled && usableFireworkHand != null) {
            if (!thrustLocked && gpwsLampColor == CONFIG.color && computer.velocityPerSecond.horizontalLength() > 0.01 && computer.pitch > (autoPilotEnabled ? 0 : 10) && !approachingDestination) {
                if (thrustSet && (computer.speed < 28 || computer.velocityPerSecond.y < -8)) {
                    mc.interactionManager.interactItem(mc.player, usableFireworkHand);
                    lastFireworkActivationTimeMs = lastUpdateTimeMs;
                }
                statusString += "THR MCT";
            } else statusString += "THR IDLE";
        }

        targetPitch = null;
        if (AutoFlightManager.autoPilotEnabled && (secondsUntilTerrainImpact <= correctThreshold || computer.velocityPerSecond.horizontalLength() < 0.01)) {
            targetPitch = -maximumSafePitch;
            statusString += "".equals(statusString) ? "GPWS" : " | GPWS";
        } else if (approachingDestination || FlightSafetyMonitor.fireworkCount <= 0 || (AutoFlightManager.autoThrustEnabled && usableFireworkHand == null)) {
            targetPitch = 2.2f;
            statusString += "".equals(statusString) ? "OPT GLD" : " | OPT GLD";
        } else if (targetAltitude != null) {
            float pitchLimiter = (float) (Math.abs(targetAltitude - mc.player.getY()) + computer.velocityPerSecond.length());
            targetPitch = (float) Math.max(-maximumSafePitch, Math.toDegrees(-Math.asin((targetAltitude - mc.player.getY()) / pitchLimiter)));
            statusString += "".equals(statusString) ? "ALT" : " | ALT";
        }


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
            statusString += "".equals(statusString) ? "A/P" : " | A/P";
        }
    }

    public static void changeLookDirection(PlayerEntity player, float pitch, float yaw) {
        player.setPitch(MathHelper.clamp(player.getPitch() + pitch, -90.0F, 90.0F));
        player.setYaw(player.getYaw() + yaw);
        player.prevPitch = MathHelper.clamp(player.prevPitch + pitch, -90.0F, 90.0F);
        player.prevYaw += yaw;
    }
}
