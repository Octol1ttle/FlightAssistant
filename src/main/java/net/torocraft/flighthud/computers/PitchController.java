package net.torocraft.flighthud.computers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class PitchController {
    private final FlightComputer computer;
    /**
     * USE MINECRAFT PITCH (minus is up and plus is down)
     **/
    public Float targetPitch = null;
    public boolean forceLevelOff = false;

    public PitchController(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick(float delta) {
        if (forceLevelOff) {
            smoothSetPitch(0.0f, delta / Math.min(1.0f, computer.gpws.impactTime));
            return;
        }

        smoothSetPitch(targetPitch, delta);
    }

    public void smoothSetPitch(Float pitch, float delta) {
        if (pitch == null) {
            return;
        }

        PlayerEntity player = computer.getPlayer();

        checkFloatValidity(pitch, "Target pitch");
        float newPitch = MathHelper.clamp(player.getPitch() + (pitch - player.getPitch()) * delta, -90.0f, 90.0f);
        checkFloatValidity(newPitch, "Calculated pitch");

        player.setPitch(newPitch);
    }

    private void checkFloatValidity(Float f, String name) {
        if (f.isNaN() || f.isInfinite() || f < -90.0f || f > 90.0f) {
            throw new IllegalArgumentException(name + " out of bounds: " + f);
        }
    }
}
