package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import static net.torocraft.flighthud.FlightHud.CONFIG_SETTINGS;

public class GPWSOffAlert extends Alert {
    @Override
    public boolean shouldActivate() {
        return !CONFIG_SETTINGS.gpws;
    }

    @Override
    public int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight) {
        return drawCaution(mc, context, x, y, highlight, "GPWS OFF");
    }
}
