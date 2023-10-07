package net.torocraft.flighthud.alerts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

public interface IAlert {
    boolean isTriggered();

    @NotNull AlertSoundData getAlertSoundData();

    void renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight);
}
