package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlightComputer {
  private static final float TICKS_PER_SECOND = 20;

  private float previousRollAngle = 0.0f;

  public Vec3d velocity;
  public float speed;
  public float pitch;
  public float heading;
  public float flightPitch;
  public float flightYaw;
  public float flightHeading;
  public float roll;
  public float altitude;
  public Integer groundLevel;
  public Float distanceFromGround;
  public Float elytraHealth;

  public void update(MinecraftClient client, float partial) {
    velocity = client.player.getVelocity();
    pitch = computePitch(client, partial);
    speed = computeSpeed(client);
    roll = computeRoll(client);
    heading = computeHeading(client);
    altitude = computeAltitude(client);
    groundLevel = computeGroundLevel(client);
    distanceFromGround = computeDistanceFromGround(altitude, groundLevel);
    flightPitch = computeFlightPitch(velocity, pitch);
    flightYaw = computeFlightYaw(velocity, client.player.getYaw(partial));
    flightHeading = toHeading(flightYaw);
    elytraHealth = computeElytraHealth(client);
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
    if (velocity.length() < 0.01) {
      return yaw;
    }
    return (float) Math.toDegrees(-Math.atan2(velocity.x, velocity.z));
  }

  /**
   * Roll logic is from:
   * <a href="https://github.com/Jorbon/cool_elytra/blob/main/src/main/java/edu/jorbonism/cool_elytra/mixin/GameRendererMixin.java">...</a>
   * to enable both mods will sync up when used together.
   */
  private float computeRoll(MinecraftClient client) {
    if (!FlightHud.CONFIG_SETTINGS.calculateRoll) {
      return 0;
    }

    float wingPower = FlightHud.CONFIG_SETTINGS.rollTurningForce;
    float rollSmoothing = FlightHud.CONFIG_SETTINGS.rollSmoothing;
    Vec3d facing = client.player.getRotationVecClient();
    Vec3d velocity = client.player.getVelocity();
    double horizontalFacing2 = facing.horizontalLengthSquared();
    double horizontalSpeed2 = velocity.horizontalLengthSquared();

    float rollAngle = 0.0f;

    if (horizontalFacing2 > 0.0D && horizontalSpeed2 > 0.0D) {
      double dot = (velocity.x * facing.x + velocity.z * facing.z) / Math.sqrt(horizontalFacing2 * horizontalSpeed2);
      dot = MathHelper.clamp(dot, -1, 1);
      double direction = Math.signum(velocity.x * facing.z - velocity.z * facing.x);
      rollAngle = (float) (Math.atan(Math.sqrt(horizontalSpeed2) * Math.acos(dot) * wingPower) * direction
              * 57.29577951308);
    }

    rollAngle = (float) ((1.0 - rollSmoothing) * rollAngle + rollSmoothing * previousRollAngle);
    previousRollAngle = rollAngle;

    return rollAngle;
  }

  private float computePitch(MinecraftClient client, float parital) {
    return client.player.getPitch(parital) * -1;
  }

  public static boolean isGround(BlockPos pos, MinecraftClient client) {
    BlockState block = client.world.getBlockState(pos);
    return !block.isAir();
  }

  public BlockPos findGround(MinecraftClient client) {
    BlockPos pos = client.player.getBlockPos();
    while (pos.getY() >= -64) {
      pos = pos.down();
      if (isGround(pos, client)) {
        return pos;
      }
    }
    return null;
  }

  private Integer computeGroundLevel(MinecraftClient client) {
    BlockPos ground = findGround(client);
    return ground == null ? null : ground.getY();
  }

  private Float computeDistanceFromGround(float altitude,
                                          Integer groundLevel) {
    if (groundLevel == null) {
      return 0.0f;
    }
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
