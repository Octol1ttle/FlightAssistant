package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;
import static net.torocraft.flighthud.HudComponent.CONFIG;

public class GPWSOffAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return !CONFIG_SETTINGS.gpws;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        int i = drawCaution(mc, context, x, y, highlight, "GPWS OFF");
        i += HudComponent.drawFont(mc, context, "   BELOW 320M", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -A/P: DO NOT USE", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, "   BELOW 320M", x, y += 9, CONFIG.adviceColor);
        i += HudComponent.drawFont(mc, context, " -A/THR: DO NOT USE", x, y + 9, CONFIG.adviceColor);
        return i;
    }
}
