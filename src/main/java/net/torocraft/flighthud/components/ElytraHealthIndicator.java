package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.torocraft.flighthud.AlertSoundInstance;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;
import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.FlightHud.killSwitch;

public class ElytraHealthIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;
  private final List<String> information = new ArrayList<>();
  private final List<String> alerts = new ArrayList<>();

  private static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flighthud:stick_shaker"));
  private static final SoundEvent PULL_UP_TERRAIN = SoundEvent.of(new Identifier("flighthud:pull_up_terrain"));
  private static final SoundEvent ELYTRA_LOW = SoundEvent.of(new Identifier("flighthud:elytra_low"));
  private static final SoundEvent AUTOPILOT_DISCONNECT = SoundEvent.of(new Identifier("flighthud:autopilot_disconnect"));
  private boolean pullUpActive = false;
  private boolean stickShakerActive = false;
  private boolean lowElytraHealthAlarm = false;
  private boolean killSwitchActive = false;
  private boolean autopilotWarningActive = false;

  private boolean autopilotEngaged = false;
  private int autopilotDestX;
  private int autopilotDestZ;
  private int autopilotCruiseAltitude;
  private long lastFireworkActivationTimeMs = 0;
  private double distanceToDest = -1;

  public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
    this.dim = dim;
    this.computer = computer;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    float x = dim.wScreen * CONFIG.elytra_x;
    float y = dim.hScreen * CONFIG.elytra_y;

    if (mc.player == null || mc.player.isOnGround() || !mc.player.isFallFlying() || computer.elytraHealth == null) {
      stopEverything(mc);

      return;
    }

    if (killSwitch.wasPressed()) {
      if (autopilotWarningActive)
        stopAutopilotWarning(mc);
      else if (autopilotEngaged) {
        autopilotEngaged = false;
        playApDisconnect(mc);
      } else
        killSwitchActive = !killSwitchActive;
    }

    if (!killSwitchActive) {
      updateStallWarning(mc, partial);
      updateLowElytraHealthAlarm(mc);
      if (autopilotEngaged)
        updateAutopilot(mc, partial);
      else {
        if (autopilotWarningActive) drawAlert("AUTO FLT A/P OFF");
        updateGPWS(mc, partial);
      }
    } else {
      stopEverything(mc);

      drawMessage("STALL & HEALTH ALARMS SILENCED");
      drawMessage("GPWS WARNINGS SILENCED");
      drawMessage("AUTO-GCAS INHIBITED");
    }

    drawEcam(mc, m, y);

    if (CONFIG.elytra_showHealth) {
      drawBox(m, x - 3.5f, y - 1.5f, 30);
      drawFont(mc, m, "E", x - 10, y);
      drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, computer.elytraHealth <= CONFIG_SETTINGS.lowElytraHealthAlarmThreshold ? CONFIG.alertColor : CONFIG.color);
    }
  }

  private void updateLowElytraHealthAlarm(MinecraftClient mc) {
    if (CONFIG_SETTINGS.lowElytraHealthAlarm && computer.elytraHealth <= CONFIG_SETTINGS.lowElytraHealthAlarmThreshold) {
      drawAlert("ELYTRA HEALTH LOW - LAND ASAP");
      if (!lowElytraHealthAlarm) {
        playRepeating(mc, ELYTRA_LOW, 0.5f);
        lowElytraHealthAlarm = true;
      }
    } else stopHealthAlarm(mc);
  }

  private void updateStallWarning(MinecraftClient mc, float tickDelta) {
    if (computer.velocity.y * TICKS_PER_SECOND <= -10 && computer.pitch > 0) {
      disconnectAutopilot(mc);
      drawAlert("STALL");
      if (CONFIG_SETTINGS.stickShaker && !stickShakerActive) {
        playRepeating(mc, STICK_SHAKER, 0.75f);
        stickShakerActive = true;
      }

      if (CONFIG_SETTINGS.stickPusher && computer.velocity.y * TICKS_PER_SECOND * -2 >= computer.distanceFromGround) {
        drawMessage("AUTO-GCAS");
        mc.player.changeLookDirection(0, computer.velocity.y * TICKS_PER_SECOND * -tickDelta);
      }
    } else stopStickShaker(mc);
  }

  private void updateGPWS(MinecraftClient mc, float tickDelta) {
    double descentSpeed = -computer.velocity.y * TICKS_PER_SECOND;
    if (computer.pitch < -2 && !stickShakerActive && descentSpeed > 10) {
      if (CONFIG_SETTINGS.gpwsTextAlerts && descentSpeed * 10 >= computer.distanceFromGround)
        drawAlert("SINKRATE");

      if (descentSpeed * 2 >= computer.distanceFromGround) {
        if (CONFIG_SETTINGS.autoGcas) {
          stopPullUp(mc);
          drawMessage("AUTO-GCAS");
          mc.player.changeLookDirection(0, Math.min(-1, computer.pitch) * tickDelta);
        }
        return;
      } else if (descentSpeed * 5 >= computer.distanceFromGround) {
        if (CONFIG_SETTINGS.gpwsTextAlerts)
          drawAlert("PULL UP");
        if (CONFIG_SETTINGS.gpwsVoiceAlerts && !pullUpActive) {
          playRepeating(mc, PULL_UP_TERRAIN, 0.75f);
          pullUpActive = true;
        }
        return;
      }
    }

    if (descentSpeed <= 7 && computer.velocity.horizontalLength() * TICKS_PER_SECOND <= 10) {
      stopPullUp(mc);
      return;
    }
    BlockHitResult terrain = raycast(mc, tickDelta, -computer.flightPitch, computer.flightYaw, 10);
    if (terrain.getType() != HitResult.Type.BLOCK || computer.groundLevel != null && Math.abs(terrain.getBlockPos().getY() - computer.groundLevel) <= 2) {
      stopPullUp(mc);
      return;
    }

    if (CONFIG_SETTINGS.gpwsTextAlerts)
      drawAlert("OBSTACLE AHEAD");

    if (CONFIG_SETTINGS.autoGcas && computer.pitch < 55 && (computer.velocity.y * TICKS_PER_SECOND > -10 || computer.pitch < 0) && mc.player.getPos().distanceTo(terrain.getBlockPos().toCenterPos()) <= computer.speed * 2 && raycast(mc, tickDelta, -55, mc.player.getYaw(), 5).getType() != HitResult.Type.BLOCK) { // Auto-GCAS
      stopPullUp(mc);
      drawMessage("AUTO-GCAS");
      mc.player.changeLookDirection(0, (55 - computer.pitch) * -tickDelta);
    } else if (mc.player.getPos().distanceTo(terrain.getBlockPos().toCenterPos()) <= computer.speed * 5) { // "Obstacle ahead, pull up!"
      if (CONFIG_SETTINGS.gpwsTextAlerts)
        drawAlert("PULL UP");
      if (CONFIG_SETTINGS.gpwsVoiceAlerts && !pullUpActive) {
        playRepeating(mc, PULL_UP_TERRAIN, 0.75f);
        pullUpActive = true;
      }
    } else stopPullUp(mc);
  }

  private void updateAutopilot(MinecraftClient mc, float tickDelta) {
    drawMessage("A/P ON - " + killSwitch.getBoundKeyLocalizedText().getString() + " TO DISENGAGE");

    BlockPos.Mutable mutable = new BlockPos.Mutable(autopilotDestX, 320 + 1, autopilotDestZ);
    Vec3d cruise = new Vec3d(mutable.getX() + 0.5, autopilotCruiseAltitude, mutable.getZ() + 0.5);

    if (isPosLoaded(mc.player.world, mutable)) {
      mutable = findLandingPos(mc, mutable);
      if (mutable.getY() <= -255)
        disconnectAutopilot(mc);
      else {
        Vec3d dest = new Vec3d(mutable.getX() + 0.5, mutable.getY(), mutable.getZ() + 0.5);

        BlockHitResult toDest = mc.world.raycast(new RaycastContext(mc.player.getPos(), dest, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, mc.player));
        if (mc.player.getY() + 0.5 > dest.y && (toDest.getType() != HitResult.Type.BLOCK || Math.abs(toDest.getPos().getY() - dest.y) <= 2)) {
          double horizontalDistance = dest.subtract(mc.player.getPos()).horizontalLength();

          float targetYaw = (float) Math.toDegrees(Math.atan2(-(dest.x - mc.player.getX()), dest.z - mc.player.getZ()));
          float targetPitch;

          if (distanceToDest < 0)
            distanceToDest = horizontalDistance;
          else
            distanceToDest = Math.min(distanceToDest, horizontalDistance);

          if (distanceToDest <= 7.5) {
            drawMessage("FLARE");
            targetPitch = -40;
          } else {
            drawMessage("LAND");
            targetPitch = (float) Math.toDegrees(-Math.asin((dest.y - mc.player.getY()) / dest.distanceTo(mc.player.getPos())));
          }

          if (Float.isNaN(targetPitch) || Float.isNaN(targetYaw)) {
            disconnectAutopilot(mc);
            return;
          }
          mc.player.changeLookDirection((targetYaw - mc.player.getYaw()) * tickDelta, (Math.max(-55, Math.min(35, targetPitch)) - mc.player.getPitch()) * tickDelta);
        } else {
          drawMessage("APPR");
          flyToCruiseAltitude(mc, tickDelta, dest.y > mc.player.getY(), new Vec3d(dest.x, Math.max(cruise.y, Math.max(dest.y, toDest.getPos().y) + 10), dest.z), 25);
        }
      }
    } else {
      drawMessage("ALT CRZ");
      flyToCruiseAltitude(mc, tickDelta, true, cruise, 15);
    }
  }

  private void flyToCruiseAltitude(MinecraftClient mc, float tickDelta, boolean allowFireworks, Vec3d dest, int maxTolerableDistanceDeviation) {
    double currentDistance = dest.subtract(mc.player.getPos()).horizontalLength();

    if (distanceToDest < 0)
      distanceToDest = currentDistance;
    else
      distanceToDest = Math.min(distanceToDest, currentDistance);

    if (currentDistance - maxTolerableDistanceDeviation > distanceToDest) { // It appears we are flying away from the destination
      disconnectAutopilot(mc);
      return;
    }

    float targetPitch = allowFireworks ? (float) Math.max(-55, Math.min(35, Math.toDegrees(-Math.asin((dest.y - mc.player.getY()) / (Math.abs(dest.y - mc.player.getY()) + computer.velocity.horizontalLength() * TICKS_PER_SECOND))))) : 2;
    float targetYaw = (float) Math.toDegrees(Math.atan2(-(dest.x - mc.player.getX()), dest.z - mc.player.getZ()));

    if (!(mc.player.getMainHandStack().getItem() instanceof FireworkRocketItem) && !(mc.player.getOffHandStack().getItem() instanceof FireworkRocketItem)) {
      drawAlert("AUTO FLT A/THR UNAVAILABLE");
      targetPitch = 2;
    }

    BlockHitResult toTargetPath = raycast(mc, tickDelta, targetPitch, targetYaw, 5);

    if (toTargetPath.getType() == HitResult.Type.BLOCK) {
      BlockPos highest = findLandingPos(mc, toTargetPath.getBlockPos().mutableCopy().setY(320 + 1));
      double targetY = Math.max(dest.y, highest.getY() + 10);
      Vec3d avoidVec = new Vec3d(highest.getX() + 0.5, targetY, highest.getZ() + 0.5);
      double distanceToObstacle = avoidVec.distanceTo(mc.player.getPos());
      if (dest.distanceTo(mc.player.getPos()) > distanceToObstacle) {
        allowFireworks = true;
        targetPitch = (float) Math.min(35, Math.toDegrees(-Math.asin((targetY - mc.player.getY()) / distanceToObstacle)));
      }
    }

    if (Float.isNaN(targetPitch) || Float.isNaN(targetYaw)) {
      disconnectAutopilot(mc);
      return;
    }
    mc.player.changeLookDirection((targetYaw - mc.player.getYaw()) * tickDelta, (targetPitch - mc.player.getPitch()) * tickDelta);

    if (!allowFireworks) return;

    if (computer.speed < 30) {
      long currentTime = Util.getMeasuringTimeMs();
      if (targetPitch < -10 && currentTime - lastFireworkActivationTimeMs > 1000) {
        if (mc.player.getMainHandStack().getItem() instanceof FireworkRocketItem) {
          mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
          lastFireworkActivationTimeMs = currentTime;
        } else if (mc.player.getOffHandStack().getItem() instanceof FireworkRocketItem) {
          mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
          lastFireworkActivationTimeMs = currentTime;
        }
      }
    }
  }

  public BlockHitResult raycast(MinecraftClient mc, float tickDelta, float pitch, float yaw, int seconds) {
    Vec3d vec3d = mc.player.getCameraPosVec(tickDelta);
    Vec3d vec3d2 = Vec3d.fromPolar(pitch, yaw);
    float time = Math.min(10, seconds);
    Vec3d vec3d3 = vec3d.add(vec3d2.x * TICKS_PER_SECOND * time, vec3d2.y * TICKS_PER_SECOND * time, vec3d2.z * TICKS_PER_SECOND * time);
    return mc.player.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, mc.player));
  }

  private BlockPos.Mutable findLandingPos(MinecraftClient mc, BlockPos.Mutable pos) {
    while (pos.getY() >= -64) {
      if (FlightComputer.isGround(pos.move(Direction.DOWN), mc)) {
        return pos.move(Direction.UP); // Land ABOVE the block to avoid collisions when the player is below the landing point
      }
    }
    return pos.setY(-255); // I regret to inform you that we are in the FUCKING VOID
  }

  private static boolean isPosLoaded(World world, BlockPos pos) {
    int i = ChunkSectionPos.getSectionCoord(pos.getX());
    int j = ChunkSectionPos.getSectionCoord(pos.getZ());
    WorldChunk worldChunk = world.getChunkManager().getWorldChunk(i, j);
    return worldChunk != null && worldChunk.getLevelType() != ChunkHolder.LevelType.INACCESSIBLE;
  }

  private void drawEcam(MinecraftClient mc, MatrixStack m, float y) {
    ArrayList<String> allMessages = new ArrayList<>(information.size() + alerts.size());
    allMessages.addAll(information);
    allMessages.addAll(alerts);
    float drawY = y - 10;
    for (int i = allMessages.size() - 1; i >= 0; i--) {
      String s = allMessages.get(i);
      drawCenteredFont(mc, m, s, dim.wScreen, drawY, information.contains(s) ? CONFIG.color : CONFIG.alertColor);
      drawY -= 10;
    }
    information.clear();
    alerts.clear();
  }

  public void setAutopilotSettings(MinecraftClient mc, int destX, int destZ, int cruiseAltitude) {
    stopAutopilotWarning(mc);
    stopPullUp(mc);
    autopilotEngaged = true;
    autopilotDestX = destX;
    autopilotDestZ = destZ;
    autopilotCruiseAltitude = cruiseAltitude;
    distanceToDest = -1;
  }

  private void disconnectAutopilot(MinecraftClient mc) {
    if (!autopilotEngaged) return;

    autopilotEngaged = false;
    if (!autopilotWarningActive) {
      playRepeating(mc, AUTOPILOT_DISCONNECT, 1);
      autopilotWarningActive = true;
    }
  }

  public void stopEverything(MinecraftClient mc) {
    stopPullUp(mc);
    stopStickShaker(mc);
    stopHealthAlarm(mc);
    stopAutopilotWarning(mc);
    if (distanceToDest > -1 && distanceToDest <= 10)
      autopilotEngaged = false;
  }

  private void stopPullUp(MinecraftClient mc) {
    pullUpActive = false;
    mc.getSoundManager().stopSounds(PULL_UP_TERRAIN.getId(), SoundCategory.MASTER);
  }

  private void stopStickShaker(MinecraftClient mc) {
    stickShakerActive = false;
    mc.getSoundManager().stopSounds(STICK_SHAKER.getId(), SoundCategory.MASTER);
  }

  private void stopHealthAlarm(MinecraftClient mc) {
    lowElytraHealthAlarm = false;
    mc.getSoundManager().stopSounds(ELYTRA_LOW.getId(), SoundCategory.MASTER);
  }

  private void stopAutopilotWarning(MinecraftClient mc) {
    autopilotWarningActive = false;
    mc.getSoundManager().stopSounds(AUTOPILOT_DISCONNECT.getId(), SoundCategory.MASTER);
  }

  private void playApDisconnect(MinecraftClient mc) {
    mc.getSoundManager().play(new AlertSoundInstance(ElytraHealthIndicator.AUTOPILOT_DISCONNECT, (float) 1, mc.player, false));
  }

  private void playRepeating(MinecraftClient mc, SoundEvent event, float volume) {
    mc.getSoundManager().play(new AlertSoundInstance(event, volume, mc.player, true));
  }

  private void drawAlert(String s) {
    alerts.add(s);
  }

  protected void drawMessage(String s) {
    information.add(s);
  }
}
