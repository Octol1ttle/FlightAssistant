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
        if (status == StallStatus.STALL) {
            firework.activateFirework(true);
        }
        maximumSafePitch = computeMaximumSafePitch();
    }

    private StallStatus computeStalling() {
        if (data.player.isInvulnerableTo(data.player.getDamageSources().fall())) {
            return StallStatus.PLAYER_INVULNERABLE;
        }
        if (data.pitch <= 0) {
            return StallStatus.PITCH_SAFE;
        }
        if (data.fallDistance <= 3) {
            return StallStatus.FALL_DISTANCE_TOO_LOW;
        }
        if (data.velocity.horizontalLength() >= -data.velocity.y) {
            return StallStatus.AIRSPEED_SAFE;
        }
        return StallStatus.STALL;
    }

    private float computeMaximumSafePitch() {
        return status == StallStatus.STALL ? 0.0f : MathHelper.clamp(data.speed * 3, 0.0f, 90.0f);
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
        STALL,
        AIRSPEED_SAFE,
        PITCH_SAFE,
        FALL_DISTANCE_TOO_LOW,
        PLAYER_INVULNERABLE,
        UNKNOWN
    }
}
