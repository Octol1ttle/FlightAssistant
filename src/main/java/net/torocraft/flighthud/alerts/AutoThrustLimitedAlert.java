package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class AutoThrustLimitedAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return AutoFlightManager.autoThrustEnabled && FlightSafetyMonitor.usableFireworkHand == null;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawCaution(mc, context, x, y, highlight, "AUTO FLT A/THR LIMITED");
        i += HudComponent.drawFont(mc, context, " -FRWKS: SELECT", x, y + 10, CONFIG.adviceColor);
        return i;
    }
}
