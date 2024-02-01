package ru.octol1ttle.flightassistant.computers.safety;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.ITickableComputer;

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

    // TODO: use raycasts to determine which block we might crash into
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

        double speed = -data.velocityPerSecond.y;

        if (speed < MAX_SAFE_SINK_RATE) {
            return STATUS_SPEED_SAFE;
        }
        return (float) (data.heightAboveGround / speed);
    }

    private float computeTerrainImpactTime() {
        if (!data.isFlying) {
            return STATUS_UNKNOWN;
        }
        if (data.player.isInvulnerableTo(data.player.getDamageSources().flyIntoWall())) {
            return STATUS_PLAYER_INVULNERABLE;
        }
        Vec3d end = data.position.add(data.velocityPerSecond.multiply(TERRAIN_RAYCAST_AHEAD_SECONDS));

        BlockHitResult result = data.world.raycast(new RaycastContext(data.position, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, data.player));
        if (result.getType() != HitResult.Type.BLOCK || result.getSide() == Direction.UP || result.getSide() == Direction.DOWN) {
            return STATUS_NO_TERRAIN_AHEAD;
        }

        double speed = data.velocityPerSecond.horizontalLength();

        if (speed < MAX_SAFE_GROUND_SPEED) {
            return STATUS_SPEED_SAFE;
        }
        return (float) (result.getPos().subtract(data.position).horizontalLength() / speed);
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
