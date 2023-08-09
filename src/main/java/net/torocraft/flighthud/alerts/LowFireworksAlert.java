package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.FlightSafetyMonitor;

public class LowFireworksAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.fireworkCount > 0 && FlightSafetyMonitor.fireworkCount < 24;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        return drawCaution(mc, context, x, y, highlight, "FWOB BELOW 24");
    }
}
