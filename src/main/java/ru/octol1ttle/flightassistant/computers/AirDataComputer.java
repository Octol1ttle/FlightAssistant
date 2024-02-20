package ru.octol1ttle.flightassistant.computers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import ru.octol1ttle.flightassistant.FAMathHelper;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class AirDataComputer implements ITickableComputer {
    private final MinecraftClient mc;
    public ClientPlayerEntity player;

    public boolean isFlying = false;
    public Vec3d position = Vec3d.ZERO;
    public Vec3d velocity = Vec3d.ZERO;
    public Vec3d velocityPerSecond = Vec3d.ZERO;
    public float speed;
    public float pitch;
    public float yaw;
    public float heading;
    public float flightPitch;
    public float flightYaw;
    public float flightHeading;
    public float roll;
    public float altitude;
    public int voidLevel = Integer.MIN_VALUE;
    public int groundLevel;
    public float heightAboveGround;
    public Float elytraHealth;
    public float fallDistance;
    public World world;

    public AirDataComputer(MinecraftClient mc, ClientPlayerEntity player) {
        this.mc = mc;
        this.player = player;
        this.world = this.player.getWorld();
    }

    public boolean canAutomationsActivate() {
        return canAutomationsActivate(true);
    }

    public boolean canAutomationsActivate(boolean checkFlying) {
        return (!checkFlying || isFlying) && mc.currentScreen == null && mc.getOverlay() == null;
    }

    public boolean isGround(BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return !block.isAir();
    }

    @Override
    public void tick() {
        assert mc.player != null;
        player = mc.player;

        isFlying = player.isFallFlying();
        position = player.getPos();
        velocity = player.getVelocity();
        velocityPerSecond = velocity.multiply(TICKS_PER_SECOND);
        pitch = computePitch();
        yaw = computeYaw();
        speed = computeSpeed();
        heading = computeHeading();
        altitude = computeAltitude();
        voidLevel = computeVoidLevel();
        groundLevel = computeGroundLevel();
        heightAboveGround = computeDistanceFromGround(altitude, groundLevel);
        flightPitch = computeFlightPitch(velocity, pitch);
        flightYaw = computeFlightYaw(velocity, yaw);
        flightHeading = toHeading(flightYaw);
        elytraHealth = computeElytraHealth();
        fallDistance = player.fallDistance;
        world = player.getWorld();
    }

    public void updateRoll(Matrix3f normal) {
        roll = computeRoll(normal);
    }

    private Float computeElytraHealth() {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack != null && stack.getItem().equals(Items.ELYTRA)) {
            float remain = (float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();
            return remain * 100.0f;
        }
        return null;
    }

    private float computeFlightPitch(Vec3d velocity, float pitch) {
        if (velocity.length() < 0.01) {
            return pitch;
        }
        Vec3d n = velocity.normalize();
        return 90 - FAMathHelper.toDegrees(Math.acos(n.y));
    }

    private float computeFlightYaw(Vec3d velocity, float yaw) {
        if (velocity.horizontalLength() < 0.01) {
            return yaw;
        }
        return FAMathHelper.toDegrees(Math.atan2(-velocity.x, velocity.z));
    }

    private float computeRoll(Matrix3f normalMatrix) {
        float y = normalMatrix.getRowColumn(0, 1);
        float x = normalMatrix.getRowColumn(1, 1);
        return FAMathHelper.toDegrees(Math.atan2(y, x));
    }

    private float computePitch() {
        return -MathHelper.wrapDegrees(player.getPitch());
    }

    private float computeYaw() {
        return MathHelper.wrapDegrees(player.getYaw());
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
        return ground == null ? voidLevel : ground.getY();
    }

    private int computeVoidLevel() {
        return world.getBottomY() - 64;
    }

    private float computeDistanceFromGround(float altitude,
                                            Integer groundLevel) {
        return Math.max(0.0f, altitude - groundLevel);
    }

    private float computeAltitude() {
        return (float) position.y;
    }

    private float computeHeading() {
        return toHeading(yaw);
    }

    private float computeSpeed() {
        return (float) velocityPerSecond.length();
    }

    public static float toHeading(float yawDegrees) {
        return yawDegrees + 180.0f;
    }

    @Override
    public String getId() {
        return "air_data";
    }

    @Override
    public void reset() {
        isFlying = false;
        position = Vec3d.ZERO;
        velocity = Vec3d.ZERO;
        velocityPerSecond = Vec3d.ZERO;
        speed = 0.0f;
        pitch = 0.0f;
        yaw = 0.0f;
        heading = 0.0f;
        flightPitch = 0.0f;
        flightYaw = 0.0f;
        flightHeading = 0.0f;
        roll = 0.0f;
        altitude = 0.0f;
        voidLevel = Integer.MIN_VALUE;
        groundLevel = 0;
        heightAboveGround = 0.0f;
        elytraHealth = null;
        fallDistance = 0.0f;
        world = player.getWorld();
    }
}
