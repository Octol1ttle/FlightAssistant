package ru.octol1ttle.flightassistant.computers.safety;

import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.computers.autoflight.PitchController;
import ru.octol1ttle.flightassistant.indicators.PitchIndicator;

public class VoidLevelComputer implements ITickableComputer {
    public static final int STATUS_REACHED_DAMAGE_LEVEL = 2;
    public static final int STATUS_APPROACHING_DAMAGE_LEVEL = 1;
    public static final int STATUS_ALTITUDE_SAFE = -1;
    public static final int STATUS_NOT_ABOVE_VOID = -2;
    public static final int STATUS_PLAYER_INVULNERABLE = -3;
    private static final int OPTIMUM_ALTITUDE_PRESERVATION_PITCH = 10;
    private final AirDataComputer data;
    private final FireworkController firework;
    private final StallComputer stall;
    public PitchController pitch;

    public int status;
    public float minimumSafePitch;

    public VoidLevelComputer(AirDataComputer data, FireworkController firework, StallComputer stall) {
        this.data = data;
        this.firework = firework;
        this.stall = stall;
    }

    public void tick() {
        status = computeStatus();
        if (status >= STATUS_ALTITUDE_SAFE && data.altitude - data.voidLevel < 12) {
            pitch.forceClimb = firework.activateFirework(true);
        }
        minimumSafePitch = computeMinimumSafePitch();
    }

    private int computeStatus() {
        if (data.player.isInvulnerableTo(data.player.getDamageSources().outOfWorld())) {
            return STATUS_PLAYER_INVULNERABLE;
        }

        if (data.groundLevel != data.voidLevel) {
            return STATUS_NOT_ABOVE_VOID;
        }

        if (data.altitude - data.voidLevel >= 8) {
            return STATUS_ALTITUDE_SAFE;
        }

        if (data.altitude >= data.voidLevel) {
            return STATUS_APPROACHING_DAMAGE_LEVEL;
        }

        return STATUS_REACHED_DAMAGE_LEVEL;
    }

    private float computeMinimumSafePitch() {
        if (status <= STATUS_NOT_ABOVE_VOID) {
            return -90.0f;
        }
        if (data.altitude - data.voidLevel < 20) {
            return Math.min(OPTIMUM_ALTITUDE_PRESERVATION_PITCH, stall.maximumSafePitch);
        }

        return PitchIndicator.DANGEROUS_DOWN_PITCH + 10;
    }

    @Override
    public String getId() {
        return "void_level";
    }
}
