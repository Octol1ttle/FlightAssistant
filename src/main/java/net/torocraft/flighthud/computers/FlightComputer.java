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
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class FlightComputer {
    @NotNull
    private final MinecraftClient mc;
    public final GPWSComputer gpws;
    public final AutoFlightComputer autoflight;
    public final TimeComputer time;
    public final PitchController pitchController;
    public final AlertController alertController;

    public Vec3d position;
    public Vec3d velocity;
    public Vec3d velocityPerSecond;
    @Nullable
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
    public int worldHeight;

    public FlightComputer(@NotNull MinecraftClient mc) {
        this.mc = mc;

        gpws = new GPWSComputer(this);
        autoflight = new AutoFlightComputer(this);
        time = new TimeComputer();
        pitchController = new PitchController(this);
        alertController = new AlertController(this, mc.getSoundManager());
    }

    public @NotNull PlayerEntity getPlayer() {
        assert mc.player != null;
        return mc.player;
    }

    public boolean isGround(BlockPos pos) {
        assert mc.world != null;
        BlockState block = mc.world.getBlockState(pos);
        return !block.isAir();
    }

    public void tick() {
        position = getPlayer().getPos();
        if (velocity != null) {
            acceleration = getPlayer().getVelocity().subtract(velocity);
        }
        velocity = getPlayer().getVelocity();
        velocityPerSecond = velocity.multiply(TICKS_PER_SECOND);
        pitch = computePitch();
        speed = computeSpeed();
        heading = computeHeading();
        altitude = computeAltitude();
        groundLevel = computeGroundLevel();
        distanceFromGround = computeDistanceFromGround(altitude, groundLevel);
        flightPitch = computeFlightPitch(velocity, pitch);
        flightYaw = computeFlightYaw(velocity, getPlayer().getYaw());
        flightHeading = toHeading(flightYaw);
        elytraHealth = computeElytraHealth();
        worldHeight = getPlayer().getWorld().getHeight();

        if (!getPlayer().isFallFlying()) {
            return;
        }

        gpws.tick();
        autoflight.tick();
        alertController.tick();
    }

    public void updateRoll(Matrix3f normal) {
        roll = computeRoll(normal);
    }

    public void onRender() {
        if (!getPlayer().isFallFlying()) {
            return;
        }

        time.tick();
        pitchController.tick(time.deltaTime);
    }

    private Float computeElytraHealth() {
        ItemStack stack = getPlayer().getEquippedStack(EquipmentSlot.CHEST);
        if (stack != null && stack.getItem().equals(Items.ELYTRA)) {
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
        return -getPlayer().getPitch();
    }

    public BlockPos findGround() {
        BlockPos.Mutable pos = getPlayer().getBlockPos().mutableCopy();
        while (pos.getY() >= -64) {
            if (isGround(pos.move(Direction.DOWN))) {
                return pos;
            }
        }
        return null;
    }

    private int computeGroundLevel() {
        BlockPos ground = findGround();
        return ground == null ? Math.min(getPlayer().getBlockY() - 4, -50) : ground.getY();
    }

    private float computeDistanceFromGround(float altitude,
                                            Integer groundLevel) {
        return Math.max(-64f, altitude - groundLevel);
    }

    private float computeAltitude() {
        return (float) getPlayer().getPos().y - 1;
    }

    private float computeHeading() {
        return toHeading(getPlayer().getYaw());
    }

    private float computeSpeed() {
        return (float) velocityPerSecond.length();
    }

    private float toHeading(float yawDegrees) {
        return (yawDegrees + 180) % 360;
    }
}
