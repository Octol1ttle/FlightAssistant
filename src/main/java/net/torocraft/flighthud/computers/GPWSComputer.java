package net.torocraft.flighthud.computers;

import net.minecraft.util.math.Vec3d;
import net.torocraft.flighthud.Util;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class GPWSComputer {
    private static final int MAX_SAFE_SINKRATE = -10;
    private static final int STATUS_VERTICAL_SPEED_SAFE = -1;
    private static final int MAX_SIMULATION_TICKS = 10 * TICKS_PER_SECOND;
    private static final int STATUS_SIMULATION_TIME_EXCEEDED = -2;
    private static final float PITCH_CORRECT_THRESHOLD = 2.5f;
    private final FlightComputer computer;
    private float impactTime;

    public GPWSComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        impactTime = this.computeImpactTime();
    }

    public float getImpactTime() {
        return impactTime;
    }

    public boolean shouldCorrectPitch() {
        return getImpactTime() <= PITCH_CORRECT_THRESHOLD;
    }

    private float computeImpactTime() {
        if (computer.velocity.y > MAX_SAFE_SINKRATE) {
            return STATUS_VERTICAL_SPEED_SAFE;
        }

        Vec3d position = Util.copyVec3d(computer.position);
        Vec3d velocity = Util.copyVec3d(computer.velocity);
        for (int ticks = 0; ticks < MAX_SIMULATION_TICKS; ticks++) {
            velocity.add(computer.acceleration);
            position.add(velocity);
            if (position.y <= computer.groundLevel) {
                return (float) ticks / TICKS_PER_SECOND;
            }
        }

        return STATUS_SIMULATION_TIME_EXCEEDED;
    }
}
