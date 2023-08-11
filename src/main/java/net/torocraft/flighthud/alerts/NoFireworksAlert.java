package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class NoFireworksAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.fireworkCount <= 0;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawWarning(mc, context, x, y, highlight, "NO FRWKS ON BOARD");
        if (AutoFlightManager.targetAltitude != null)
            i += HudComponent.drawFont(mc, context, " -AP ALT: RESET", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " GLDG DIST: 100 BLKS/10 GND ALT", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
