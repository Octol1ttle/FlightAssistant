package net.torocraft.flighthud.config;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.FlightHud;
import net.torocraft.flighthud.config.loader.IConfig;

public class SettingsConfig implements IConfig {

  public enum DisplayMode {
    NONE, MIN, FULL
  }

  public boolean watchForConfigChanges = true;
  public String displayModeWhenFlying = DisplayMode.FULL.toString();
  public String displayModeWhenNotFlying = DisplayMode.NONE.toString();
  public boolean calculateRoll = true;

  public boolean gpwsTextAlerts = true;
  public boolean gpwsVoiceAlerts = true;
  public boolean autoGcas = true;
  public boolean stickShaker = true;
  public boolean stickPusher = true;
  public boolean lowElytraHealthAlarm = true;
  public int lowElytraHealthAlarmThreshold = 10;

  @Override
  public void update() {
  }

  @Override
  public boolean shouldWatch() {
    return watchForConfigChanges;
  }

  private static String toggle(String curr) {
    DisplayMode m = parseDisplayMode(curr);
    int i = (m.ordinal() + 1) % DisplayMode.values().length;
    return DisplayMode.values()[i].toString();
  }

  public void toggleDisplayMode() {
    MinecraftClient client = MinecraftClient.getInstance();

    if (client.player.isFallFlying()) {
      displayModeWhenFlying = toggle(displayModeWhenFlying);
    } else {
      displayModeWhenNotFlying = toggle(displayModeWhenNotFlying);
    }

    FlightHud.CONFIG_LOADER_SETTINGS.save(this);
  }

  public static DisplayMode parseDisplayMode(String s) {
    try {
      return DisplayMode.valueOf(s);
    } catch (Exception e) {
      return DisplayMode.NONE;
    }
  }

}
