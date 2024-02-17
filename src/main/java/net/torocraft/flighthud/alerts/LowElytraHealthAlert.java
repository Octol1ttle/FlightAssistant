package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.shims.DrawContext;

public class LowElytraHealthAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.isElytraLow;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        return drawWarning(mc, context, x, y, highlight, "ELYTRA HEALTH LOW");
    }
}
