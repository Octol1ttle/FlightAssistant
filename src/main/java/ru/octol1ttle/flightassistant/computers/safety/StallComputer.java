package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

public class StallComputer implements ITickableComputer {
    private final FireworkController firework;
    private final AirDataComputer data;
    public StallStatus status = StallStatus.UNKNOWN;
    public float maximumSafePitch = 90.0f;

    public StallComputer(FireworkController firework, AirDataComputer data) {
        this.firework = firework;
        this.data = data;
    }

    public void tick() {
        status = computeStalling();
        if (status == StallStatus.FULL_STALL) {
            firework.activateFirework(true);
        }
        maximumSafePitch = computeMaximumSafePitch();
    }

    private StallStatus computeStalling() {
        if (data.pitch <= 0) {
            return StallStatus.PITCH_SAFE;
        }
        if (data.fallDistance <= 3) {
            return StallStatus.FALL_DISTANCE_TOO_LOW;
        }
        if (data.velocity.horizontalLength() >= -data.velocity.y) {
            return StallStatus.AIRSPEED_SAFE;
        }
        if (-data.velocityPerSecond.y < GPWSComputer.MAX_SAFE_SINK_RATE) {
            return StallStatus.APPROACHING_STALL;
        }
        return StallStatus.FULL_STALL;
    }

    private float computeMaximumSafePitch() {
        return status == StallStatus.FULL_STALL ? 0.0f : MathHelper.clamp(data.speed * 3, 0.0f, 90.0f);
    }

    public boolean anyStall() {
        return status == StallComputer.StallStatus.APPROACHING_STALL
                || status == StallComputer.StallStatus.FULL_STALL;
    }

    @Override
    public String getId() {
        return "stall_det";
    }

    @Override
    public void reset() {
        status = StallStatus.UNKNOWN;
        maximumSafePitch = 90.0f;
    }

    public enum StallStatus {
        FULL_STALL,
        APPROACHING_STALL,
        PITCH_SAFE,
        AIRSPEED_SAFE,
        FALL_DISTANCE_TOO_LOW,
        UNKNOWN
    }
}
