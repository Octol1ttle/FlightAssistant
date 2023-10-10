package net.torocraft.flighthud.computers;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class GPWSComputer {
    public static final int MAX_SAFE_SINK_RATE = 10;
    public static final float PITCH_CORRECT_THRESHOLD = 2.5f;
    private static final int MAX_SAFE_GROUND_SPEED = 15;
    private static final int STATUS_ACCELERATION_NOT_AVAILABLE = -1;
    private static final int STATUS_FALL_DISTANCE_TOO_LOW = -2;
    private static final int STATUS_SPEED_SAFE = -3;
    private static final int STATUS_NO_TERRAIN_AHEAD = -4;
    private static final float BLOCK_PITCH_CONTROL_THRESHOLD = 5.0f;
    private static final float TERRAIN_RAYCAST_AHEAD_SECONDS = 10.0f;
    private final FlightComputer computer;
    public float descentImpactTime;
    public float terrainImpactTime;

    public GPWSComputer(FlightComputer computer) {
        this.computer = computer;
    }

    public void tick() {
        descentImpactTime = this.computeDescentImpactTime();
        terrainImpactTime = this.computeTerrainImpactTime();

        computer.pitchController.forceLevelOff = computer.gpws.shouldLevelOff();
        if (computer.pitchController.forceLevelOff) {
            // Looks like it's trying to kill us.
            // should this trigger alternate law?
            computer.autoflight.disconnectAutopilot(true);
        }
    }

    public boolean shouldLevelOff() {
        return descentImpactTime > 0 && descentImpactTime <= PITCH_CORRECT_THRESHOLD;
    }

    public boolean shouldBlockPitchChanges() {
        return descentImpactTime > 0 && descentImpactTime <= BLOCK_PITCH_CONTROL_THRESHOLD;
    }

    private float computeDescentImpactTime() {
        if (computer.acceleration == null) {
            return STATUS_ACCELERATION_NOT_AVAILABLE;
        }
        if (computer.player.fallDistance <= 3) {
            return STATUS_FALL_DISTANCE_TOO_LOW;
        }
        if (-computer.velocityPerSecond.y < MAX_SAFE_SINK_RATE) {
            return STATUS_SPEED_SAFE;
        }

        return getTimeWithAcceleration(-computer.velocityPerSecond.y, -computer.acceleration.y * TICKS_PER_SECOND, computer.distanceFromGround);
    }

    private float computeTerrainImpactTime() {
        if (computer.acceleration == null) {
            return STATUS_ACCELERATION_NOT_AVAILABLE;
        }
        if (computer.velocityPerSecond.horizontalLength() < MAX_SAFE_GROUND_SPEED) {
            return STATUS_SPEED_SAFE;
        }

        Vec3d accelerationVector = computer.acceleration.multiply(TICKS_PER_SECOND);
        Vec3d end = computer.position.add(computer.velocityPerSecond.multiply(TERRAIN_RAYCAST_AHEAD_SECONDS).add(accelerationVector.multiply(TERRAIN_RAYCAST_AHEAD_SECONDS * 0.5f)));

        BlockHitResult result = computer.player.getWorld().raycast(new RaycastContext(computer.position, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, computer.player));
        if (result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP || result.getSide() == Direction.DOWN) {
            return STATUS_NO_TERRAIN_AHEAD;
        }

        return getTimeWithAcceleration(computer.velocityPerSecond.horizontalLength(), accelerationVector.horizontalLength(), result.getPos().subtract(computer.position).horizontalLength());
    }

    private float getTimeWithAcceleration(double initialSpeed, double acceleration, double path) {
        return (float) ((-initialSpeed + Math.sqrt(Math.pow(initialSpeed, 2) + 2 * acceleration * path))
                / acceleration);
    }
}
