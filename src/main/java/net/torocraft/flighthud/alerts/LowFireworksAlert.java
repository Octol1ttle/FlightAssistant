package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class LowFireworksAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.fireworkCount > 0 && FlightSafetyMonitor.fireworkCount < 24;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawCaution(mc, context, x, y, highlight, "FRWK CNT BELOW 24");
        if (AutoFlightManager.autoThrustEnabled)
            i += HudComponent.drawFont(mc, context, " -A/THR: OFF", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -CLIMB: INITIATE", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
