package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.FlightSafetyMonitor;

public class PassengerDismountedAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return FlightSafetyMonitor.havePassengersDismounted;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        return drawWarning(mc, context, x, y, highlight, "PAX DISMOUNTED");
    }
}