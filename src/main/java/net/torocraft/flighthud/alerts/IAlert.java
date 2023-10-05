package net.torocraft.flighthud.alerts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public interface IAlert {
    boolean isTriggered();

    void tick();

    int drawText(MinecraftClient mc, DrawContext context, float x, float y, boolean highlight);
}
