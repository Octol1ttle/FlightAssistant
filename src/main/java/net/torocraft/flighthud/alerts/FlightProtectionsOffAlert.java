package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.shims.DrawContext;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class FlightProtectionsOffAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return !FlightSafetyMonitor.flightProtectionsEnabled;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawCaution(mc, context, x, y, highlight, "FLIGHT PROTECTIONS OFF");
        i += HudComponent.drawFont(mc, context, " MAX PITCH: 40* UP", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " MIN V/S: -8 BPS", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -FRWKS: CONFIRM SAFE", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " MANEUVER WITH CARE", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
