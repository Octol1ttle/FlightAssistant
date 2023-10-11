package net.torocraft.flighthud.computers;

import net.minecraft.util.math.MathHelper;

public class StallComputer {
    public static final int STATUS_APPROACHING_STALL = 1;
    private static final int STATUS_FULL_STALL = 2;
    private static final int STATUS_PITCH_SAFE = -1;
    private static final int STATUS_FALL_DISTANCE_TOO_LOW = -2;
    private static final int STATUS_AIRSPEED_SAFE = -3;
    private final FlightComputer computer;
    public int stalling;
    public float maximumSafePitch;

    public StallComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        stalling = computeStalling();
        maximumSafePitch = computeMaximumSafePitch();
    }

    private int computeStalling() {
        if (computer.pitch <= 0) {
            return STATUS_PITCH_SAFE;
        }
        if (computer.player.fallDistance <= 3) {
            return STATUS_FALL_DISTANCE_TOO_LOW;
        }
        if (computer.velocity.horizontalLength() >= -computer.velocity.y) {
            return STATUS_AIRSPEED_SAFE;
        }
        if (-computer.velocityPerSecond.y < GPWSComputer.MAX_SAFE_SINK_RATE) {
            return STATUS_APPROACHING_STALL;
        }
        return STATUS_FULL_STALL;
    }

    private float computeMaximumSafePitch() {
        return stalling == STATUS_FULL_STALL ? 0.0f : MathHelper.clamp(computer.speed * 3, 0.0f, 90.0f);
    }
}
