package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

public class StallComputer implements ITickableComputer {
    private static final int STATUS_FULL_STALL = 2;
    public static final int STATUS_APPROACHING_STALL = 1;
    private static final int STATUS_PITCH_SAFE = -1;
    private static final int STATUS_AIRSPEED_SAFE = -2;
    private static final int STATUS_FALL_DISTANCE_TOO_LOW = -3;
    private final FireworkController firework;
    private final AirDataComputer data;
    public int stalling;
    public float maximumSafePitch;

    public StallComputer(FireworkController firework, AirDataComputer data) {
        this.firework = firework;
        this.data = data;
    }

    public void tick() {
        stalling = computeStalling();
        if (stalling == STATUS_FULL_STALL) {
            firework.activateFirework(true);
        }
        maximumSafePitch = computeMaximumSafePitch();
    }

    private int computeStalling() {
        if (data.pitch <= 0) {
            return STATUS_PITCH_SAFE;
        }
        if (data.fallDistance <= 3) {
            return STATUS_FALL_DISTANCE_TOO_LOW;
        }
        if (data.velocity.horizontalLength() >= -data.velocity.y) {
            return STATUS_AIRSPEED_SAFE;
        }
        if (-data.velocityPerSecond.y < GPWSComputer.MAX_SAFE_SINK_RATE) {
            return STATUS_APPROACHING_STALL;
        }
        return STATUS_FULL_STALL;
    }

    private float computeMaximumSafePitch() {
        return stalling == STATUS_FULL_STALL ? 0.0f : MathHelper.clamp(data.speed * 3, 0.0f, 90.0f);
    }

    @Override
    public String getId() {
        return "stall_det";
    }
}
