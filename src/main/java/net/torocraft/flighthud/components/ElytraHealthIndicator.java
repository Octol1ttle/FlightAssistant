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
  private static final SoundEvent CAUTION_TERRAIN = SoundEvent.of(new Identifier("flighthud:caution_terrain"));
  private boolean auralWarningActive = false;
  private boolean canToga = true;
  private boolean terrainAhead = false;

  public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
    this.dim = dim;
    this.computer = computer;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    float x = dim.wScreen * CONFIG.elytra_x;
    float y = dim.hScreen * CONFIG.elytra_y;
    if (computer.velocity.y * TICKS_PER_SECOND <= -5) {
      if (computer.pitch >= CONFIG.pitchLadder_optimumClimbAngle + 10) {
        ItemStack main = mc.player.getMainHandStack();
        boolean toga;

        if (!main.getItem().equals(Items.FIREWORK_ROCKET) || (main.getSubNbt("Fireworks") != null && !main.getSubNbt("Fireworks").contains("Explosions", 10))) {
          ItemStack off = mc.player.getOffHandStack();
          toga = togaIfAble(mc, off.getItem().equals(Items.FIREWORK_ROCKET) ? Hand.OFF_HAND : Hand.MAIN_HAND);
        } else
          toga = togaIfAble(mc, Hand.MAIN_HAND);

        drawCenteredFont(mc, m, toga ? "AUTO-FIREWORK" : "NO FIREWORKS IN HAND", dim.wScreen, y - 25, CONFIG.alertColor);
      } else if (Math.abs(computer.pitch) >= CONFIG.pitchLadder_optimumClimbAngle - 10)
        drawCenteredFont(mc, m, "MONITOR PITCH", dim.wScreen, y - 25, CONFIG.alertColor);

      if (Math.abs(computer.pitch) >= CONFIG.pitchLadder_optimumClimbAngle || computer.pitch < 30 && computer.terrainBelow(mc)) {
        drawCenteredFont(mc, m, computer.pitch > 0 ? "PUSH DOWN" : "PULL UP", dim.wScreen, y - 15, CONFIG.alertColor);
        if (!auralWarningActive) {
          play(mc, computer.pitch > 0 ? STICK_SHAKER : PULL_UP);
          auralWarningActive = true;
        }
      } else resetWarnings(mc);
    } else resetWarnings(mc);

    if (!computer.terrainAhead(mc)) terrainAhead = false;
    else {
      drawCenteredFont(mc, m, "CAUTION: TERRAIN", dim.wScreen, y - 35, CONFIG.alertColor);
      if (!auralWarningActive && !terrainAhead) {
        play(mc, CAUTION_TERRAIN);
        terrainAhead = true;
      }
    }

    if (!CONFIG.elytra_showHealth || computer.elytraHealth == null) {
      return;
    }

    drawBox(m, x - 3.5f, y - 1.5f, 30);
    drawFont(mc, m, "E", x - 10, y);
    drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, computer.elytraHealth <= 10 ? CONFIG.alertColor : CONFIG.color);
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
