package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.HudComponent.CONFIG;

public class ThrustLockedAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.thrustLocked;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawCaution(mc, context, x, y, highlight, "THRUST LOCKED");
        if (!CONFIG_SETTINGS.gpws || !AutoFlightManager.autoPilotEnabled)
            i += HudComponent.drawFont(mc, context, "-GPWS+AP: ON", x, y += 9, CONFIG.adviceColor);
        if (!AutoFlightManager.autoThrustEnabled) {
            i += HudComponent.drawFont(mc, context, "-ATHR: ON THEN OFF", x, y + 9, CONFIG.adviceColor);
            return i;
        }
        i += HudComponent.drawFont(mc, context, "-ATHR: OFF", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
