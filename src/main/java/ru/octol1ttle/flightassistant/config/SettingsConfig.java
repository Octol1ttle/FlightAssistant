package ru.octol1ttle.flightassistant.config;

import net.minecraft.client.MinecraftClient;
import ru.octol1ttle.flightassistant.FlightAssistant;
import ru.octol1ttle.flightassistant.config.loader.IConfig;

public class SettingsConfig implements IConfig {

    public boolean watchForConfigChanges = true;
    public String displayModeWhenFlying = DisplayMode.FULL.toString();
    public String displayModeWhenNotFlying = DisplayMode.NONE.toString();
    public boolean calculateRoll = true;

    private static String toggle(String curr) {
        DisplayMode m = parseDisplayMode(curr);
        int i = (m.ordinal() + 1) % DisplayMode.values().length;
        return DisplayMode.values()[i].toString();
    }

    public static DisplayMode parseDisplayMode(String s) {
        try {
            return DisplayMode.valueOf(s);
        } catch (Exception e) {
            return DisplayMode.NONE;
        }
    }

    @Override
    public void update() {
    }

    @Override
    public boolean shouldWatch() {
        return watchForConfigChanges;
    }

    public void toggleDisplayMode(MinecraftClient client) {
        assert client.player != null;
        if (client.player.isFallFlying()) {
            displayModeWhenFlying = toggle(displayModeWhenFlying);
        } else {
            displayModeWhenNotFlying = toggle(displayModeWhenNotFlying);
        }

        FlightAssistant.CONFIG_LOADER_SETTINGS.save(this);
    }

    public enum DisplayMode {
        NONE, MIN, FULL
    }

}
