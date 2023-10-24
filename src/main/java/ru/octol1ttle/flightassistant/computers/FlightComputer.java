package ru.octol1ttle.flightassistant.computers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import ru.octol1ttle.flightassistant.FlightAssistant;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class FlightComputer {
    @NotNull
    private final MinecraftClient mc;
    public final GPWSComputer gpws;
    public final AutoFlightComputer autoflight;
    public final TimeComputer time;
    public final StallComputer stall;
    public final VoidDamageLevelComputer voidDamage;
    public final PitchController pitchController;
    public final AlertController alertController;
    @NotNull
    public PlayerEntity player;

    public Vec3d position;
    public Vec3d velocity;
    public Vec3d velocityPerSecond;
    @Nullable
    public Vec3d acceleration;
    public float speed;
    public float pitch;
    public float yaw;
    public float heading;
    public float flightPitch;
    public float flightYaw;
    public float flightHeading;
    public float roll;
    public float altitude;
    public int voidLevel;
    public int groundLevel;
    public float distanceFromGround;
    public Float elytraHealth;
    public int worldHeight;

    public FlightComputer(@NotNull MinecraftClient mc) {
        this.mc = mc;
        assert mc.player != null;
        this.player = mc.player;

        this.gpws = new GPWSComputer(this);
        this.autoflight = new AutoFlightComputer(this);
        this.stall = new StallComputer(this);
        this.voidDamage = new VoidDamageLevelComputer(this);
        this.time = new TimeComputer();
        this.pitchController = new PitchController(this);
        this.alertController = new AlertController(this, mc.getSoundManager());
    }

    public boolean isGround(BlockPos pos) {
        assert mc.world != null;
        BlockState block = mc.world.getBlockState(pos);
        return !block.isAir();
    }

    public void tick() {
        assert mc.player != null;
        player = mc.player;

        // TODO: sanity checks?
        position = player.getPos();
        if (velocity != null) {
            acceleration = player.getVelocity().subtract(velocity);
        }
        velocity = player.getVelocity();
        velocityPerSecond = velocity.multiply(TICKS_PER_SECOND);
        pitch = computePitch();
        yaw = computeYaw();
        speed = computeSpeed();
        heading = computeHeading();
        altitude = computeAltitude();
        voidLevel = computeVoidLevel();
        groundLevel = computeGroundLevel();
        distanceFromGround = computeDistanceFromGround(altitude, groundLevel);
        flightPitch = computeFlightPitch(velocity, pitch);
        flightYaw = computeFlightYaw(velocity, yaw);
        flightHeading = toHeading(flightYaw);
        elytraHealth = computeElytraHealth();
        worldHeight = player.getWorld().getHeight();

        if (!player.isFallFlying()) {
            return;
        }

        gpws.tick();
        autoflight.tick();
        alertController.tick();
        stall.tick();
        voidDamage.tick();
    }

    public void updateRoll(Matrix3f normal) {
        roll = computeRoll(normal);
    }

    public void onRender() {
        time.tick();
        if (!shouldUpdatePitch()) {
            return;
        }

        pitchController.tick(time.deltaTime);
    }

    public boolean shouldUpdatePitch() {
        return player.isFallFlying() && mc.currentScreen == null && mc.getOverlay() == null;
    }

    private Float computeElytraHealth() {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
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
        if (!FlightAssistant.CONFIG_SETTINGS.calculateRoll) {
            return 0.0f;
        }

        float y = normalMatrix.getRowColumn(0, 1);
        float x = normalMatrix.getRowColumn(1, 1);
        return (float) Math.toDegrees(Math.atan2(y, x));
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
        return player.getWorld().getBottomY() - 64;
    }

    private float computeDistanceFromGround(float altitude,
                                            Integer groundLevel) {
        return Math.max(-64f, altitude - groundLevel);
    }

    private float computeAltitude() {
        return (float) player.getPos().y - 1;
    }

    private float computeHeading() {
        return toHeading(yaw);
    }

    private float computeSpeed() {
        return (float) velocityPerSecond.length();
    }

    private float toHeading(float yawDegrees) {
        return yawDegrees + 180.0f;
    }
}
