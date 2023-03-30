package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class ElytraHealthIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;
  private static final SoundEvent STICK_SHAKER = SoundEvent.of(new Identifier("flighthud:stick_shaker"));
  private static final SoundEvent PULL_UP = SoundEvent.of(new Identifier("flighthud:pull_up"));
  private static final SoundEvent PULL_UP_TERRAIN = SoundEvent.of(new Identifier("flighthud:pull_up_terrain"));
  private static final SoundEvent ELYTRA_LOW = SoundEvent.of(new Identifier("flighthud:elytra_low"));
  private boolean auralWarningActive = false;
  private boolean elytraAlarmActive = false;
  private boolean canToga = true;
  private boolean terrainAhead = false;
  private boolean gcas = false;
  private float gcasPitch = 0.0f;

  public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
    this.dim = dim;
    this.computer = computer;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    float x = dim.wScreen * CONFIG.elytra_x;
    float y = dim.hScreen * CONFIG.elytra_y;
    if (computer.velocity.y * TICKS_PER_SECOND <= -5) {
      if (Math.abs(computer.pitch) >= 45)
        drawCenteredFont(mc, m, "MONITOR PITCH", dim.wScreen, y - 25, CONFIG.alertColor);

      boolean terrainBelow = computer.terrainBelow(mc, 7);
      if (Math.abs(computer.pitch) >= 55 || (computer.pitch <= -40 && terrainBelow)) {
        if (!gcas && ((Math.abs(computer.pitch) >= 65 || (computer.pitch <= -50 && terrainBelow)) || computer.terrainBelow(mc, 2))) {
          gcas = true;
          gcasPitch = computer.pitch;
        } else {
          if (!auralWarningActive) {
            play(mc, computer.pitch > 0 ? STICK_SHAKER : PULL_UP);
            auralWarningActive = true;
          }
          drawCenteredFont(mc, m, computer.pitch > 0 ? "PUSH DOWN" : "PULL UP", dim.wScreen, y - 15, CONFIG.alertColor);
        }

      } else resetWarnings(mc);
    } else resetWarnings(mc);

    if (gcas) {
      if (gcasPitch > 0 ? computer.pitch <= 0 : computer.pitch >= 0)
        gcas = false;
      else {
        drawCenteredFont(mc, m, "GCAS", dim.wScreen, y - 15, CONFIG.alertColor);
        mc.player.changeLookDirection(0, Math.max(1, Math.abs(computer.pitch)) * Math.signum(computer.pitch) * partial);
      }
    }

    if (computer.terrainAhead(mc, 12))
      drawCenteredFont(mc, m, "OBSTACLE AHEAD", dim.wScreen, y - 35, CONFIG.alertColor);
    if (computer.terrainAhead(mc, 7)) {
      if (!terrainAhead && (!auralWarningActive || computer.pitch > 0)) {
        play(mc, PULL_UP_TERRAIN);
        terrainAhead = true;
      }
    } else {
        mc.getSoundManager().stopSounds(PULL_UP_TERRAIN.getId(), SoundCategory.MASTER);
        terrainAhead = false;
    }

    if (computer.elytraHealth == null) {
      mc.getSoundManager().stopSounds(ELYTRA_LOW.getId(), SoundCategory.MASTER);
      elytraAlarmActive = false;
      return;
    }

    if (CONFIG.elytra_showHealth) {
      drawBox(m, x - 3.5f, y - 1.5f, 30);
      drawFont(mc, m, "E", x - 10, y);
      drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, computer.elytraHealth <= 10 ? CONFIG.alertColor : CONFIG.color);
    }

    if (computer.elytraHealth <= 5) {
      if (!elytraAlarmActive) {
        play(mc, ELYTRA_LOW);
        elytraAlarmActive = true;
      }
    } else {
      mc.getSoundManager().stopSounds(ELYTRA_LOW.getId(), SoundCategory.MASTER);
      elytraAlarmActive = false;
    }
  }

  private boolean togaIfAble(MinecraftClient mc, Hand hand) {
    if (!canToga) return false;
    mc.interactionManager.interactItem(mc.player, hand);
    canToga = false;
    return true;
  }

  private void resetWarnings(MinecraftClient mc) {
    canToga = true;
    mc.getSoundManager().stopSounds(STICK_SHAKER.getId(), SoundCategory.MASTER);
    mc.getSoundManager().stopSounds(PULL_UP.getId(), SoundCategory.MASTER);
    auralWarningActive = false;
  }

  private void play(MinecraftClient mc, SoundEvent event) {
    mc.getSoundManager().play(new EntityTrackingSoundInstance(event, SoundCategory.MASTER, 1, 1, mc.player, 0));
  }
}
