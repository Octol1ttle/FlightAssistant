package net.torocraft.flighthud.computers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.torocraft.flighthud.FlightHud;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class FlightComputer {
    public final GPWSComputer gpws = new GPWSComputer(this);
    @NotNull
    private final MinecraftClient mc;
    @NotNull
    private final PlayerEntity player;
    public AutoFlightComputer autoflight = new AutoFlightComputer(this);
    public PitchController pitchController = new PitchController();

    public Vec3d position;
    public Vec3d velocity;
    public Vec3d velocityPerSecond;
    public Vec3d acceleration;
    public float speed;
    public float pitch;
    public float heading;
    public float flightPitch;
    public float flightYaw;
    public float flightHeading;
    public float roll;
    public float altitude;
    public int groundLevel;
    public float distanceFromGround;
    public Float elytraHealth;

    public FlightComputer(MinecraftClient mc) {
        this.mc = mc;
        assert mc.player != null;
        this.player = mc.player;
    }

    public boolean isGround(BlockPos pos) {
        assert mc.world != null;
        BlockState block = mc.world.getBlockState(pos);
        return !block.isAir();
    }

    public void tick() {
        position = player.getPos();
        acceleration = player.getVelocity().subtract(velocity);
        velocity = player.getVelocity();
        velocityPerSecond = velocity.multiply(TICKS_PER_SECOND);
        pitch = computePitch();
        speed = computeSpeed();
        heading = computeHeading();
        altitude = computeAltitude();
        groundLevel = computeGroundLevel();
        distanceFromGround = computeDistanceFromGround(altitude, groundLevel);
        flightPitch = computeFlightPitch(velocity, pitch);
        flightYaw = computeFlightYaw(velocity, player.getYaw());
        flightHeading = toHeading(flightYaw);
        elytraHealth = computeElytraHealth();

        gpws.tick();
        autoflight.tick();
    }

    public void updateRoll(Matrix3f normal) {
        roll = computeRoll(normal);
    }

    private Float computeElytraHealth() {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack != null && stack.getItem() == Items.ELYTRA) {
            float remain = ((float) stack.getMaxDamage() - (float) stack.getDamage()) / (float) stack.getMaxDamage();
            return remain * 100f;
        }
        return null;
    }

    private float computeFlightPitch(Vec3d velocity, float pitch) {
        if (velocity.length() < 0.01) {
            return pitch;
        }
        Vec3d n = velocity.normalize();
        return (float) (90 - Math.toDegrees(Math.acos(n.y)));
    }

    private float computeFlightYaw(Vec3d velocity, float yaw) {
        if (velocity.horizontalLength() < 0.01) {
            return yaw;
        }
        return (float) Math.toDegrees(Math.atan2(-velocity.x, velocity.z));
    }

    private float computeRoll(Matrix3f normalMatrix) {
        if (!FlightHud.CONFIG_SETTINGS.calculateRoll) {
            return 0.0f;
        }

        float y = normalMatrix.getRowColumn(0, 1);
        float x = normalMatrix.getRowColumn(1, 1);
        return (float) Math.toDegrees(Math.atan2(y, x));
    }

    private float computePitch() {
        return -player.getPitch();
    }

    public BlockPos findGround() {
        BlockPos.Mutable pos = player.getBlockPos().mutableCopy();
        while (pos.getY() >= -64) {
            if (isGround(pos.move(Direction.DOWN))) {
                return pos;
            }
        }
        return null;
    }

    private int computeGroundLevel() {
        BlockPos ground = findGround();
        return ground == null ? Math.min(player.getBlockY() + 4, -50) : ground.getY();
    }

    private float computeDistanceFromGround(float altitude,
                                            Integer groundLevel) {
        return Math.max(-64f, altitude - groundLevel);
    }

    private float computeAltitude() {
        return (float) player.getPos().y - 1;
    }

    private float computeHeading() {
        return toHeading(player.getYaw());
    }

    private float computeSpeed() {
        return (float) velocityPerSecond.length();
    }

    private float toHeading(float yawDegrees) {
        return (yawDegrees + 180) % 360;
    }

    public void tickPitchController(float tickDelta) {
        pitchController.tick(player, tickDelta);
    }
}
