package ru.octol1ttle.flightassistant.computers.safety;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;

public class VoidLevelComputer implements ITickableComputer {
    private static final int OPTIMUM_ALTITUDE_PRESERVATION_PITCH = 15;
    private final AirDataComputer data;
    private final FireworkController firework;
    private final StallComputer stall;
    public VoidLevelStatus status = VoidLevelStatus.UNKNOWN;
    public float minimumSafePitch = -90.0f;

    public VoidLevelComputer(AirDataComputer data, FireworkController firework, StallComputer stall) {
        this.data = data;
        this.firework = firework;
        this.stall = stall;
    }

    public void tick() {
        status = computeStatus();
        if (aboveVoid() && data.altitude - data.voidLevel < 12) {
            firework.activateFirework(true);
        }
        minimumSafePitch = computeMinimumSafePitch();
    }

    private VoidLevelStatus computeStatus() {
        if (!data.isFlying) {
            return VoidLevelStatus.UNKNOWN;
        }
        if (data.player.isInvulnerableTo(data.player.getDamageSources().outOfWorld())) {
            return VoidLevelStatus.PLAYER_INVULNERABLE;
        }

        if (data.groundLevel != data.voidLevel) {
            return VoidLevelStatus.NOT_ABOVE_VOID;
        }

        if (data.altitude - data.voidLevel >= 8) {
            return VoidLevelStatus.ALTITUDE_SAFE;
        }

        if (data.altitude >= data.voidLevel) {
            return VoidLevelStatus.APPROACHING_DAMAGE_LEVEL;
        }

        return VoidLevelStatus.REACHED_DAMAGE_LEVEL;
    }

    private float computeMinimumSafePitch() {
        if (status == VoidLevelStatus.UNKNOWN || status == VoidLevelStatus.PLAYER_INVULNERABLE || status == VoidLevelStatus.NOT_ABOVE_VOID) {
            return -90.0f;
        }
        if (data.altitude - data.voidLevel < 16) {
            return Math.min(OPTIMUM_ALTITUDE_PRESERVATION_PITCH, stall.maximumSafePitch);
        }

        return -PitchController.DESCEND_PITCH + 10;
    }

    public boolean aboveVoid() {
        return status == VoidLevelStatus.ALTITUDE_SAFE || approachingOrReachedDamageLevel();
    }

    public boolean approachingOrReachedDamageLevel() {
        return status == VoidLevelStatus.APPROACHING_DAMAGE_LEVEL || status == VoidLevelStatus.REACHED_DAMAGE_LEVEL;
    }

    @Override
    public String getId() {
        return "void_level";
    }

    @Override
    public void reset() {
        status = VoidLevelStatus.UNKNOWN;
        minimumSafePitch = -90.0f;
    }

    public enum VoidLevelStatus {
        REACHED_DAMAGE_LEVEL,
        APPROACHING_DAMAGE_LEVEL,
        ALTITUDE_SAFE,
        NOT_ABOVE_VOID,
        PLAYER_INVULNERABLE,
        UNKNOWN
    }
}
