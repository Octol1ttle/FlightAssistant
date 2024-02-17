package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.shims.DrawContext;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class NoFireworksAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.fireworkCount <= 0;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawWarning(mc, context, x, y, highlight, "FRWK CNT ZERO");
        if (AutoFlightManager.autoThrustEnabled)
            i += HudComponent.drawFont(mc, context, " -A/THR: OFF", x, y += 9, CONFIG.adviceColor);
        if (AutoFlightManager.targetAltitude != null)
            i += HudComponent.drawFont(mc, context, " -A/P ALT: RESET", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " GLDG DIST:", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, "   100 BLKS/10 M", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
