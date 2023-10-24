package net.torocraft.flighthud.computers;

import net.torocraft.flighthud.indicators.PitchIndicator;

public class VoidDamageLevelComputer {
    public static final int STATUS_REACHED_DAMAGE_LEVEL = 2;
    public static final int STATUS_APPROACHING_DAMAGE_LEVEL = 1;
    public static final int STATUS_ALTITUDE_SAFE = -1;
    public static final int STATUS_NOT_ABOVE_VOID = -2;
    public static final int STATUS_PLAYER_INVULNERABLE = -3;
    private static final int OPTIMUM_ALTITUDE_PRESERVATION_PITCH = 10;

    private final FlightComputer computer;
    public int status;
    public float minimumSafePitch;

    public VoidDamageLevelComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        status = computeStatus();
        minimumSafePitch = computeMinimumSafePitch();
    }

    private int computeStatus() {
        if (computer.player.isInvulnerableTo(computer.player.getDamageSources().outOfWorld())) {
            return STATUS_PLAYER_INVULNERABLE;
        }

        if (computer.groundLevel != computer.voidLevel) {
            return STATUS_NOT_ABOVE_VOID;
        }

        if (computer.altitude - computer.voidLevel >= 8) {
            return STATUS_ALTITUDE_SAFE;
        }

        if (computer.altitude >= computer.voidLevel) {
            return STATUS_APPROACHING_DAMAGE_LEVEL;
        }

        // TODO: TOGA LK
        return STATUS_REACHED_DAMAGE_LEVEL;
    }

    private float computeMinimumSafePitch() {
        if (status <= STATUS_NOT_ABOVE_VOID) {
            return -90.0f;
        }
        if (computer.altitude - computer.voidLevel < 20) {
            return Math.min(OPTIMUM_ALTITUDE_PRESERVATION_PITCH, computer.stall.maximumSafePitch);
        }

        return PitchIndicator.DANGEROUS_DOWN_PITCH + 10;
    }
}
