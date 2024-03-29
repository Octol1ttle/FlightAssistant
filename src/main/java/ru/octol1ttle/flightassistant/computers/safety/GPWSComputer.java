package ru.octol1ttle.flightassistant.computers.safety;

import java.awt.Color;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class GPWSComputer implements ITickableComputer {
    private static final int STATUS_PLAYER_INVULNERABLE = -1;
    private static final int STATUS_FALL_DISTANCE_TOO_LOW = -2;
    private static final int STATUS_SPEED_SAFE = -3;
    private static final int STATUS_NO_TERRAIN_AHEAD = -4;
    private static final int STATUS_UNKNOWN = -5;
    private static final float FIREWORK_SPEED = 33.62f;
    private static final float TERRAIN_RAYCAST_AHEAD_SECONDS = 10.0f;
    private static final float MAX_SAFE_GROUND_SPEED = 17.5f;
    private static final float MAX_SAFE_SINK_RATE = 10.0f;
    private static final float CAUTION_THRESHOLD = 10.0f;
    private static final float PULL_UP_THRESHOLD = 5.0f;
    private static final float PITCH_CORRECT_THRESHOLD = 2.5f;
    private final AirDataComputer data;
    private final FlightPlanner plan;
    public float descentImpactTime = STATUS_UNKNOWN;
    public float terrainImpactTime = STATUS_UNKNOWN;
    public LandingClearanceStatus landingClearanceStatus = LandingClearanceStatus.UNKNOWN;
    public boolean fireworkUseSafe = true;

    public GPWSComputer(AirDataComputer data, FlightPlanner plan) {
        this.data = data;
        this.plan = plan;
    }

    @Override
    public void tick() {
        descentImpactTime = this.computeDescentImpactTime();
        terrainImpactTime = this.computeTerrainImpactTime();
        landingClearanceStatus = this.computeLandingClearanceStatus();
        fireworkUseSafe = this.computeFireworkUseSafe();
    }

    public boolean isInDanger() {
        return getGPWSLampColor() == FAConfig.indicator().warningColor;
    }

    public boolean shouldCorrectSinkrate() {
        return FAConfig.computer().sinkrateProtection.recover() && positiveLessOrEquals(descentImpactTime, PITCH_CORRECT_THRESHOLD);
    }

    public boolean shouldCorrectTerrain() {
        return FAConfig.computer().terrainProtection.recover() && positiveLessOrEquals(terrainImpactTime, PULL_UP_THRESHOLD);
    }


    public Color getGPWSLampColor() {
        if (positiveLessOrEquals(descentImpactTime, PULL_UP_THRESHOLD) || positiveLessOrEquals(terrainImpactTime, PULL_UP_THRESHOLD)) {
            return FAConfig.indicator().warningColor;
        }
        if (positiveLessOrEquals(descentImpactTime, CAUTION_THRESHOLD) || positiveLessOrEquals(terrainImpactTime, CAUTION_THRESHOLD)) {
            return FAConfig.indicator().cautionColor;
        }

        return FAConfig.indicator().frameColor;
    }

    public boolean shouldBlockPitchChanges() {
        return FAConfig.computer().sinkrateProtection.override() && positiveLessOrEquals(descentImpactTime, PULL_UP_THRESHOLD)
                || FAConfig.computer().terrainProtection.override() && positiveLessOrEquals(terrainImpactTime, PULL_UP_THRESHOLD);
    }

    private float computeDescentImpactTime() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return STATUS_UNKNOWN;
        }
        if (data.player().isInvulnerableTo(data.player().getDamageSources().fall())) {
            return STATUS_PLAYER_INVULNERABLE;
        }
        if (data.fallDistance() <= 3.0f) {
            return STATUS_FALL_DISTANCE_TOO_LOW;
        }

        double speed = -data.velocity.y;

        if (speed < MAX_SAFE_SINK_RATE) {
            return STATUS_SPEED_SAFE;
        }
        return (float) (data.heightAboveGround() / speed);
    }

    private float computeTerrainImpactTime() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return STATUS_UNKNOWN;
        }
        if (data.player().isInvulnerableTo(data.player().getDamageSources().flyIntoWall())) {
            return STATUS_PLAYER_INVULNERABLE;
        }
        Vec3d end = data.position().add(data.velocity.multiply(TERRAIN_RAYCAST_AHEAD_SECONDS));

        BlockHitResult result = data.world().raycast(new RaycastContext(data.position(), end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player()));
        if (plan.autolandAllowed || result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP) {
            return STATUS_NO_TERRAIN_AHEAD;
        }

        double speed = data.velocity.horizontalLength();

        if (speed < MAX_SAFE_GROUND_SPEED) {
            return STATUS_SPEED_SAFE;
        }

        double distance = result.getPos().subtract(data.position()).length();
        return (float) (distance / speed);
    }

    private boolean computeFireworkUseSafe() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return true;
        }
        if (data.player().isInvulnerableTo(data.player().getDamageSources().flyIntoWall())) {
            return true;
        }
        Vec3d end = data.position().add(Vec3d.fromPolar(data.pitch(), data.yaw()).multiply(FIREWORK_SPEED * TERRAIN_RAYCAST_AHEAD_SECONDS));

        BlockHitResult result = data.world().raycast(new RaycastContext(data.position(), end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player()));
        if (result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP) {
            return true;
        }

        double distance = result.getPos().subtract(data.position()).length();

        return distance / FIREWORK_SPEED > PULL_UP_THRESHOLD;
    }

    private LandingClearanceStatus computeLandingClearanceStatus() {
        if (!data.isFlying() || data.player().isTouchingWater()) {
            return LandingClearanceStatus.UNKNOWN;
        }
        if (plan.landAltitude == null) {
            return LandingClearanceStatus.NOT_LANDING;
        }

        Double distance = plan.getDistanceToWaypoint();
        if (distance == null) {
            throw new AssertionError();
        }

        float minimumHeight = Math.min(data.heightAboveGround(), Math.abs(data.altitude() - plan.landAltitude));
        if (data.velocity.y > -3.0f || distance / minimumHeight < AirDataComputer.OPTIMUM_GLIDE_RATIO) {
            return LandingClearanceStatus.SAFE;
        }

        return LandingClearanceStatus.TOO_LOW;
    }

    private boolean positiveLessOrEquals(float time, float lessOrEquals) {
        if (time < 0.0f) {
            return false;
        }

        return time <= lessOrEquals;
    }

    private BlockPos findHighest(BlockPos.Mutable at) {
        while (at.getY() < data.world().getTopY()) {
            if (!data.isGround(at.move(Direction.UP))) {
                return at;
            }
        }
        return at;
    }

    @Override
    public String getId() {
        return "gpws";
    }

    @Override
    public void reset() {
        descentImpactTime = STATUS_UNKNOWN;
        terrainImpactTime = STATUS_UNKNOWN;
        landingClearanceStatus = LandingClearanceStatus.UNKNOWN;
        fireworkUseSafe = true;
    }

    public enum LandingClearanceStatus {
        TOO_LOW,
        SAFE,
        NOT_LANDING,
        UNKNOWN
    }
}
