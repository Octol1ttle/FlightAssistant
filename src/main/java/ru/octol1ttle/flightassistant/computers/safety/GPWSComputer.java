package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;
import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class GPWSComputer implements ITickableComputer {
    public static final int MAX_SAFE_SINK_RATE = 10;
    public static final float PITCH_CORRECT_THRESHOLD = 2.5f;
    private static final int MAX_SAFE_GROUND_SPEED = 15;
    private static final int STATUS_PLAYER_INVULNERABLE = -1;
    private static final int STATUS_FALL_DISTANCE_TOO_LOW = -2;
    private static final int STATUS_SPEED_SAFE = -3;
    private static final int STATUS_NO_TERRAIN_AHEAD = -4;
    private static final int STATUS_UNKNOWN = -5;
    private static final float BLOCK_PITCH_CONTROL_THRESHOLD = 5.0f;
    private static final float TERRAIN_RAYCAST_AHEAD_SECONDS = 10.0f;
    private final AirDataComputer data;
    public float descentImpactTime = STATUS_UNKNOWN;
    public float terrainImpactTime = STATUS_UNKNOWN;

    public GPWSComputer(AirDataComputer data) {
        this.data = data;
    }

    @Override
    public void tick() {
        descentImpactTime = this.computeDescentImpactTime();
        terrainImpactTime = this.computeTerrainImpactTime();
    }

    public boolean isInDanger() {
        return getGPWSLampColor() == CONFIG.alertColor;
    }

    public boolean shouldRecover() {
        return descentImpactTime >= 0.0f && descentImpactTime <= PITCH_CORRECT_THRESHOLD
                || terrainImpactTime >= 0.0f && terrainImpactTime <= PITCH_CORRECT_THRESHOLD;
    }

    public int getGPWSLampColor() {
        if ((descentImpactTime >= 0.0f && descentImpactTime <= 5.0f) || (terrainImpactTime >= 0.0f && terrainImpactTime <= 5.0f)) {
            return CONFIG.alertColor;
        }
        if ((descentImpactTime >= 0.0f && descentImpactTime <= 10.0f) || (terrainImpactTime >= 0.0f && terrainImpactTime <= 10.0f)) {
            return CONFIG.amberColor;
        }

        return CONFIG.color;
    }

    public boolean shouldBlockPitchChanges() {
        return descentImpactTime >= 0.0f && descentImpactTime <= BLOCK_PITCH_CONTROL_THRESHOLD;
    }

    private float computeDescentImpactTime() {
        if (!data.isFlying) {
            return STATUS_UNKNOWN;
        }
        if (data.player.isInvulnerableTo(data.player.getDamageSources().fall())) {
            return STATUS_PLAYER_INVULNERABLE;
        }
        if (data.fallDistance <= 3) {
            return STATUS_FALL_DISTANCE_TOO_LOW;
        }

        double initialSpeed = -data.velocityPerSecond.y;
        double acceleration = -data.acceleration.y * TICKS_PER_SECOND;
        float time = getTimeWithAcceleration(initialSpeed, acceleration, data.heightAboveGround);

        if (getSpeedWithAcceleration(initialSpeed, acceleration, time) < MAX_SAFE_SINK_RATE) {
            return STATUS_SPEED_SAFE;
        }
        return time;
    }

    // TODO: this is terrible and always has been.
    // TODO: I don't think anyone knows how to make a proper system that doesn't spuriously trigger in one tick and shuts up the next
    // TODO: fixing terrain detection should be a high priority, but I have zero idea what to do about this
    // TODO: I mean, I'm coding this completely solo, writing these comments just helps me let off some frustration caused by code that gets reworked numerous times and still barely works
    private float computeTerrainImpactTime() {
        if (!data.isFlying) {
            return STATUS_UNKNOWN;
        }
        if (data.player.isInvulnerableTo(data.player.getDamageSources().flyIntoWall())) {
            return STATUS_PLAYER_INVULNERABLE;
        }
        // TODO: acceleration is a meaningless concept in a situation where you can do a 180* turn in less than half a second
        // TODO: well, by this logic, velocity is meaningless too (it is), but you can't do shit without velocity
        // TODO: I'm thinking of just removing terrain detection entirely (be honest, when did it ever help someone?)
        // TODO: I really shouldn't have added any of these "safety" features in the first place because they make no sense.
        // TODO: but there'll always be this one person who says they like it/they need it:
        // TODO: "ah yes I need this feature as I cannot see giant mountains in front of me in a game that has perfect visibility 100% of the time"
        Vec3d accelerationVector = data.acceleration.multiply(TICKS_PER_SECOND);
        Vec3d end = data.position.add(data.velocityPerSecond.multiply(TERRAIN_RAYCAST_AHEAD_SECONDS).add(accelerationVector.multiply(Math.pow(TERRAIN_RAYCAST_AHEAD_SECONDS, 2)).multiply(0.5f)));

        BlockHitResult result = data.world.raycast(new RaycastContext(data.position, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player));
        if (result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP || result.getSide() == Direction.DOWN) {
            return STATUS_NO_TERRAIN_AHEAD;
        }

        double initialSpeed = data.velocityPerSecond.horizontalLength();
        double acceleration = accelerationVector.horizontalLength();
        float time = getTimeWithAcceleration(initialSpeed, acceleration, result.getPos().subtract(data.position).horizontalLength());

        if (getSpeedWithAcceleration(initialSpeed, acceleration, time) < MAX_SAFE_GROUND_SPEED) {
            return STATUS_SPEED_SAFE;
        }
        return time;
    }

    private float getTimeWithAcceleration(double initialSpeed, double acceleration, double path) {
        if (acceleration <= 0.0) {
            return (float) (path / initialSpeed);
        }
        return (float) ((-initialSpeed + Math.sqrt(Math.pow(initialSpeed, 2) + 2 * acceleration * path))
                / acceleration);
    }

    private float getSpeedWithAcceleration(double initialSpeed, double acceleration, double time) {
        if (acceleration <= 0.0) {
            return (float) initialSpeed;
        }
        return (float) (initialSpeed + acceleration * time);
    }

    @Override
    public String getId() {
        return "gpws";
    }

    @Override
    public void reset() {
        descentImpactTime = STATUS_UNKNOWN;
        terrainImpactTime = STATUS_UNKNOWN;
    }
}
