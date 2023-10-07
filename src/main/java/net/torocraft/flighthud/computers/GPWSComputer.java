package net.torocraft.flighthud.computers;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class GPWSComputer {
    public static final float PITCH_CORRECT_THRESHOLD = 2.5f;
    private static final int MAX_SAFE_SINKRATE = 10;
    private static final int STATUS_VERTICAL_SPEED_SAFE = -1;
    private static final int STATUS_ACCELERATION_NOT_AVAILABLE = -2;
    private static final float BLOCK_PITCH_CONTROL_THRESHOLD = 5.0f;
    private final FlightComputer computer;
    public float impactTime;

    public GPWSComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        impactTime = this.computeImpactTime();

        if (computer.gpws.shouldCorrectPitch()) {
            // Looks like it's trying to kill us.
            computer.autoflight.disconnectAutopilot(true);
        }
        computer.pitchController.forceLevelOff = computer.gpws.shouldCorrectPitch();
    }

    public boolean shouldCorrectPitch() {
        return impactTime > 0 && impactTime <= PITCH_CORRECT_THRESHOLD;
    }

    public boolean shouldBlockPitchChanges() {
        return impactTime > 0 && impactTime <= BLOCK_PITCH_CONTROL_THRESHOLD;
    }

    private float computeImpactTime() {
        if (computer.acceleration == null) {
            return STATUS_ACCELERATION_NOT_AVAILABLE;
        }
        if (-computer.velocityPerSecond.y < MAX_SAFE_SINKRATE) {
            return STATUS_VERTICAL_SPEED_SAFE;
        }

        double initialSpeed = -computer.velocityPerSecond.y;
        double acceleration = -computer.acceleration.y * TICKS_PER_SECOND;
        return (float) ((-initialSpeed + Math.sqrt(Math.pow(initialSpeed, 2) + 2 * acceleration * computer.distanceFromGround))
                / acceleration);
    }
}
