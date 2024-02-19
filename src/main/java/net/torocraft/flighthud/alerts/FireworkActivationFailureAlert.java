package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.AutoFlightManager;
import net.torocraft.flighthud.FlightSafetyMonitor;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.shims.DrawContext;

import static net.torocraft.flighthud.AutoFlightManager.lastUpdateTimeMs;
import static net.torocraft.flighthud.HudComponent.CONFIG;

public class FireworkActivationFailureAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return !FlightSafetyMonitor.thrustSet && lastUpdateTimeMs - FlightSafetyMonitor.lastFireworkActivationTimeMs > 1000;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawWarning(mc, context, x, y, highlight, "FRWK ACTIVATION FAIL");
        if (AutoFlightManager.flightDirectorsEnabled)
            i += HudComponent.drawFont(mc, context, " -FD: OFF", x, y += 9, CONFIG.adviceColor);
        if (AutoFlightManager.autoThrustEnabled)
            i += HudComponent.drawFont(mc, context, " -A/THR: OFF", x, y += 9, CONFIG.adviceColor);
        if (AutoFlightManager.autoPilotEnabled)
            i += HudComponent.drawFont(mc, context, " -A/P: OFF", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -PITCH: MAX SAFE UP", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -FRWKS: DO NOT USE", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " SPD MAY BE UNRELIABLE", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
