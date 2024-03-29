package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class StallComputer implements ITickableComputer {
    private final FireworkController firework;
    private final AirDataComputer data;
    public StallStatus status = StallStatus.UNKNOWN;
    public float maximumSafePitch = 90.0f;

    public StallComputer(FireworkController firework, AirDataComputer data) {
        this.firework = firework;
        this.data = data;
    }

    @Override
    public void tick() {
        status = computeStalling();
        if (FAConfig.computer().stallUseFireworks && status == StallStatus.FULL_STALL) {
            firework.activateFirework(true);
        }
        maximumSafePitch = computeMaximumSafePitch();
    }

    private StallStatus computeStalling() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return StallStatus.UNKNOWN;
        }
        if (data.player().isInvulnerableTo(data.player().getDamageSources().fall())) {
            return StallStatus.PLAYER_INVULNERABLE;
        }
        if (data.pitch() <= 0.0f) {
            return StallStatus.PITCH_SAFE;
        }
        if (data.fallDistance() <= 3.0f) {
            return StallStatus.FALL_DISTANCE_TOO_LOW;
        }
        if (data.velocity.horizontalLength() >= -data.velocity.y) {
            return StallStatus.AIRSPEED_SAFE;
        }
        if (data.velocity.y > -10.0f) {
            return StallStatus.APPROACHING_STALL;
        }
        return StallStatus.FULL_STALL;
    }

    private float computeMaximumSafePitch() {
        if (!data.isFlying() || status == StallStatus.UNKNOWN || status == StallStatus.PLAYER_INVULNERABLE) {
            return 90.0f;
        }
        return status == StallStatus.FULL_STALL ? -90.0f : MathHelper.clamp(data.speed() * 3.0f, 0.0f, 90.0f);
    }

    public boolean isPitchUnsafe(float newPitch) {
        return newPitch > maximumSafePitch || status == StallStatus.FULL_STALL;
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
        AIRSPEED_SAFE,
        PITCH_SAFE,
        FALL_DISTANCE_TOO_LOW,
        PLAYER_INVULNERABLE,
        UNKNOWN
    }
}
