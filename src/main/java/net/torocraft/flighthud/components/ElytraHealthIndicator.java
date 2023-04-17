package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;
import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.FlightHud.killSwitch;

public class ElytraHealthIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;

  private static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flighthud:stick_shaker"));
  private static final SoundEvent PULL_UP_TERRAIN = SoundEvent.of(new Identifier("flighthud:pull_up_terrain"));
  private static final SoundEvent TOO_LOW_TERRAIN = SoundEvent.of(new Identifier("flighthud:too_low_terrain"));
  private static final SoundEvent ELYTRA_LOW = SoundEvent.of(new Identifier("flighthud:elytra_low"));
  private boolean pullUpActive = false;
  private boolean stickShakerActive = false;
  private boolean tooLowTerrain = false;
  private boolean lowElytraHealthAlarm = false;
  private boolean killSwitchActive = false;
  private boolean holdingKillSwitch = false;

  public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
    this.dim = dim;
    this.computer = computer;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    float x = dim.wScreen * CONFIG.elytra_x;
    float y = dim.hScreen * CONFIG.elytra_y;

    if (mc.player == null || mc.player.isOnGround() || computer.elytraHealth == null) {
      stopPullUp(mc);
      stopStickShaker(mc);
      stopTerrain(mc);
      stopHealthAlarm(mc);
      return;
    }

    if (killSwitch.wasPressed()) {
      killSwitchActive = !killSwitchActive;
    }

    if (!killSwitchActive) {
      updateStallWarning(mc, m, partial, y);
      updateGPWS(mc, m, partial, y);
      updateLowElytraHealthAlarm(mc);
    } else {
      stopPullUp(mc);
      stopStickShaker(mc);
      stopTerrain(mc);
      stopHealthAlarm(mc);

      drawCenteredFont(mc, m, "STALL & HEALTH ALARMS SILENCED", dim.wScreen, y - 35, CONFIG.alertColor);
      drawCenteredFont(mc, m, "GPWS WARNINGS SILENCED", dim.wScreen, y - 25, CONFIG.alertColor);
      drawCenteredFont(mc, m, "AUTO-GCAS INHIBITED", dim.wScreen, y - 15, CONFIG.alertColor);
    }

    if (CONFIG.elytra_showHealth) {
      drawBox(m, x - 3.5f, y - 1.5f, 30);
      drawFont(mc, m, "E", x - 10, y);
      drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, computer.elytraHealth <= 10 ? CONFIG.alertColor : CONFIG.color);
    }
  }

  private void updateLowElytraHealthAlarm(MinecraftClient mc) {
    if (computer.elytraHealth <= 10) {
      if (!lowElytraHealthAlarm) {
        play(mc, ELYTRA_LOW, 0.75f);
        lowElytraHealthAlarm = true;
      }
    } else stopHealthAlarm(mc);
  }

  private void updateStallWarning(MinecraftClient mc, MatrixStack m, float tickDelta, float y) {
    if (computer.velocity.y * TICKS_PER_SECOND <= -10 && computer.pitch > 0) {
        drawCenteredFont(mc, m, "STALL", dim.wScreen, y - 35, CONFIG.alertColor);
        if (CONFIG_SETTINGS.stickShaker && !stickShakerActive) {
          play(mc, STICK_SHAKER, 1);
          stickShakerActive = true;
        }

        if (CONFIG_SETTINGS.stickPusher && computer.velocity.y * TICKS_PER_SECOND * -2 >= computer.distanceFromGround) {
          drawCenteredFont(mc, m, "AUTO-GCAS", dim.wScreen, y - 15, CONFIG.alertColor);
          mc.player.changeLookDirection(0, computer.velocity.y * TICKS_PER_SECOND * -tickDelta);
        }
    } else stopStickShaker(mc);
  }

  private void updateGPWS(MinecraftClient mc, MatrixStack m, float tickDelta, float y) {
    double descentSpeed = -computer.velocity.y * TICKS_PER_SECOND;
    if (CONFIG_SETTINGS.autoGcas && computer.pitch < -2 && !stickShakerActive && descentSpeed * 2 >= computer.distanceFromGround) {
      drawCenteredFont(mc, m, "AUTO-GCAS", dim.wScreen, y - 15, CONFIG.alertColor);
      mc.player.changeLookDirection(0, Math.min(-1, computer.pitch) * tickDelta);
    }

    if (Math.max(descentSpeed, 15) > computer.distanceFromGround) {
      if (CONFIG_SETTINGS.gpwsTextAlerts)
        drawCenteredFont(mc, m, "TOO LOW - TERRAIN", dim.wScreen, y - 25, CONFIG.alertColor);
      if (CONFIG_SETTINGS.gpwsVoiceAlerts && !tooLowTerrain) {
        play(mc, TOO_LOW_TERRAIN, 0.5f);
        tooLowTerrain = true;
      }
      return;
    } else stopTerrain(mc);

    Vec3d vec3d = mc.player.getCameraPosVec(tickDelta);
    Vec3d vec3d2 = getRotationVec(computer.flightYaw);
    float time = Math.min(10, computer.distanceFromGround);
    Vec3d vec3d3 = vec3d.add(vec3d2.x * TICKS_PER_SECOND * time, vec3d2.y * TICKS_PER_SECOND * time, vec3d2.z * TICKS_PER_SECOND * time);
    BlockHitResult straightAhead = mc.player.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, mc.player));
    if (straightAhead.getType() != HitResult.Type.BLOCK) {
      stopPullUp(mc);
      return;
    }

    if (CONFIG_SETTINGS.gpwsTextAlerts)
      drawCenteredFont(mc, m, "OBSTACLE AHEAD", dim.wScreen, y - 25, CONFIG.alertColor);
    if (CONFIG_SETTINGS.autoGcas && !stickShakerActive && (computer.velocity.y * TICKS_PER_SECOND > -10 || computer.pitch < 0) && mc.player.getBlockPos().isWithinDistance(straightAhead.getBlockPos(), computer.speed * Math.min(2, computer.distanceFromGround))) { // Auto-GCAS
      drawCenteredFont(mc, m, "AUTO-GCAS", dim.wScreen, y - 15, CONFIG.alertColor);
      mc.player.changeLookDirection(0, (55 - computer.pitch) * -tickDelta);
    } else if (mc.player.getBlockPos().isWithinDistance(straightAhead.getBlockPos(), computer.speed * Math.min(5, computer.distanceFromGround))) { // *whoop whoop* "Pull up!"
      if (CONFIG_SETTINGS.gpwsTextAlerts && descentSpeed * 2 < computer.distanceFromGround)
        drawCenteredFont(mc, m, "PULL UP", dim.wScreen, y - 15, CONFIG.alertColor);
      if (CONFIG_SETTINGS.gpwsVoiceAlerts && !pullUpActive) {
        play(mc, PULL_UP_TERRAIN, 0.75f);
        pullUpActive = true;
      }
    } else stopPullUp(mc);
  }

  private Vec3d getRotationVec(float yaw) {
    float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
    float g = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
    return new Vec3d(-g, 0, -f);
  }

  private void stopPullUp(MinecraftClient mc) {
    pullUpActive = false;
    mc.getSoundManager().stopSounds(PULL_UP_TERRAIN.getId(), SoundCategory.MASTER);
  }

  private void stopStickShaker(MinecraftClient mc) {
    stickShakerActive = false;
    mc.getSoundManager().stopSounds(STICK_SHAKER.getId(), SoundCategory.MASTER);
  }

  private void stopTerrain(MinecraftClient mc) {
    tooLowTerrain = false;
    mc.getSoundManager().stopSounds(TOO_LOW_TERRAIN.getId(), SoundCategory.MASTER);
  }

  private void stopHealthAlarm(MinecraftClient mc) {
    lowElytraHealthAlarm = false;
    mc.getSoundManager().stopSounds(ELYTRA_LOW.getId(), SoundCategory.MASTER);
  }

  private void play(MinecraftClient mc, SoundEvent event, float volume) {
    mc.getSoundManager().play(new EntityTrackingSoundInstance(event, SoundCategory.MASTER, volume, 1, mc.player, 0));
  }
}
