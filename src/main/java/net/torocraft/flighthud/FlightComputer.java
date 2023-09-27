package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;

public class FlightComputer {
    private static final float TICKS_PER_SECOND = 20;

    public Vec3d velocity;
    public Vec3d velocityPerSecond;
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

    public static boolean isGround(BlockPos pos, MinecraftClient client) {
        BlockState block = client.world.getBlockState(pos);
        return !block.isAir();
    }

    public void update(MinecraftClient client, Matrix3f normal) {
        velocity = client.player.getVelocity();
        velocityPerSecond = velocity.multiply(TICKS_PER_SECOND);
        pitch = computePitch(client);
        speed = computeSpeed(client);
        roll = computeRoll(normal);
        heading = computeHeading(client);
        altitude = computeAltitude(client);
        groundLevel = computeGroundLevel(client);
        distanceFromGround = computeDistanceFromGround(altitude, groundLevel);
        flightPitch = computeFlightPitch(velocity, pitch);
        flightYaw = computeFlightYaw(velocity, client.player.getYaw());
        flightHeading = toHeading(flightYaw);
        elytraHealth = computeElytraHealth(client);

        AutoFlightManager.update(client, this);
        FlightSafetyMonitor.update(client, this);
    }

    private Float computeElytraHealth(MinecraftClient client) {
        ItemStack stack = client.player.getEquippedStack(EquipmentSlot.CHEST);
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

    private float computePitch(MinecraftClient client) {
        return -client.player.getPitch();
    }

    public BlockPos findGround(MinecraftClient client) {
        BlockPos.Mutable pos = client.player.getBlockPos().mutableCopy();
        while (pos.getY() >= -64) {
            if (isGround(pos.move(Direction.DOWN), client)) {
                return pos;
            }
        }
        return null;
    }

    private int computeGroundLevel(MinecraftClient client) {
        BlockPos ground = findGround(client);
        return ground == null ? Math.min(client.player.getBlockY() + 4, -50) : ground.getY();
    }

    private float computeDistanceFromGround(float altitude,
                                            Integer groundLevel) {
        return Math.max(-64f, altitude - groundLevel);
    }

    private float computeAltitude(MinecraftClient client) {
        return (float) client.player.getPos().y - 1;
    }

    private float computeHeading(MinecraftClient client) {
        return toHeading(client.player.getYaw());
    }

    private float computeSpeed(MinecraftClient client) {
        float speed;
        var player = client.player;
        if (player.hasVehicle()) {
            Entity entity = player.getVehicle();
            speed = (float) entity.getVelocity().length() * TICKS_PER_SECOND;
        } else {
            speed = (float) client.player.getVelocity().length() * TICKS_PER_SECOND;
        }
        return speed;
    }

    private float toHeading(float yawDegrees) {
        return (yawDegrees + 180) % 360;
    }
}
