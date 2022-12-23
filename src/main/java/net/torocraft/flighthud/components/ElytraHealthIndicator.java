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
  private static final Identifier STICK_SHAKER_ID = new Identifier("flighthud:stick_shaker");
  private static final SoundEvent STICK_SHAKER = SoundEvent.of(STICK_SHAKER_ID);
  private boolean stickShakerActive = false;
  private boolean canToga = true;

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
          if (!off.getItem().equals(Items.FIREWORK_ROCKET))
            toga = togaIfAble(mc, Hand.MAIN_HAND);
          else
            toga = togaIfAble(mc, Hand.OFF_HAND);
        } else toga = togaIfAble(mc, Hand.MAIN_HAND);
        drawCenteredFont(mc, m, toga ? "AUTO-FIREWORK" : "NO FIREWORKS IN HAND", dim.wScreen, y - 25, CONFIG.alertColor);
      } else if (computer.pitch >= CONFIG.pitchLadder_optimumClimbAngle - 10)
        drawCenteredFont(mc, m, "MONITOR PITCH", dim.wScreen, y - 25, CONFIG.alertColor);
      if (computer.pitch >= CONFIG.pitchLadder_optimumClimbAngle) {
        drawCenteredFont(mc, m, "PUSH DOWN", dim.wScreen, y - 15, CONFIG.alertColor);
        if (!stickShakerActive) {
          mc.getSoundManager().play(new EntityTrackingSoundInstance(STICK_SHAKER, SoundCategory.MASTER, 1, 1, mc.player, 0));
          stickShakerActive = true;
        }
      }
    } else {
      canToga = true;
      mc.getSoundManager().stopSounds(STICK_SHAKER_ID, SoundCategory.MASTER);
      stickShakerActive = false;
    }

    if (!CONFIG.elytra_showHealth || computer.elytraHealth == null) {
      return;
    }

    drawBox(m, x - 3.5f, y - 1.5f, 30);
    drawFont(mc, m, "E", x - 10, y);
    drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, computer.elytraHealth <= 10 ? CONFIG.alertColor : CONFIG.color);
  }

  public boolean togaIfAble(MinecraftClient mc, Hand hand) {
    if (computer.terrainAhead(mc) || !canToga) return false;
    mc.interactionManager.interactItem(mc.player, hand);
    canToga = false;
    return true;
  }
}
