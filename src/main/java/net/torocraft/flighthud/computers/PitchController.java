package net.torocraft.flighthud.computers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.torocraft.flighthud.indicators.PitchIndicator;

public class PitchController {
    private static final float CLIMB_PITCH = -55.0f;
    private final FlightComputer computer;
    /**
     * USE MINECRAFT PITCH (minus is up and plus is down)
     **/
    public Float targetPitch = null;
    public boolean forceLevelOff = false;
    public boolean forceClimb = false;

    public PitchController(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick(float delta) {
        if (computer.pitch > computer.stall.maximumSafePitch) {
            smoothSetPitch(-computer.stall.maximumSafePitch, delta);
            return;
        }
        if (forceLevelOff) {
            smoothSetPitch(0.0f, delta / MathHelper.clamp(computer.gpws.descentImpactTime, 0.001f, 1.0f));
            return;
        }
        if (forceClimb) {
            smoothSetPitch(CLIMB_PITCH, delta / MathHelper.clamp(computer.gpws.terrainImpactTime, 0.001f, 1.0f));
            return;
        }

        smoothSetPitch(targetPitch, delta);
    }

    /**
     * Smoothly changes the player's pitch to the specified pitch using the delta
     *
     * @param pitch Target MINECRAFT pitch (- is up, + is down)
     * @param delta Delta time, in seconds
     */
    public void smoothSetPitch(Float pitch, float delta) {
        if (pitch == null) {
            return;
        }
        checkFloatValidity(pitch, "Target pitch");

        PlayerEntity player = computer.player;
        float difference = pitch - player.getPitch();

        if (difference < 0) { // going UP
            pitch = MathHelper.clamp(pitch, -computer.stall.maximumSafePitch, 90.0f);
        }
        if (difference > 0) { // going DOWN
            pitch = MathHelper.clamp(pitch, -90.0f, -PitchIndicator.DANGEROUS_DOWN_PITCH);
        }
        checkFloatValidity(pitch, "Clamped target pitch");

        float newPitch = player.getPitch() + (pitch - player.getPitch()) * delta;
        checkFloatValidity(newPitch, "New pitch");

        player.setPitch(newPitch);
    }

    private void checkFloatValidity(Float f, String name) {
        // TODO: convert to alternate law trigger
        if (f.isNaN() || f.isInfinite() || f < -90.0f || f > 90.0f) {
            throw new IllegalArgumentException(name + " out of bounds: " + f);
        }
    }
}
