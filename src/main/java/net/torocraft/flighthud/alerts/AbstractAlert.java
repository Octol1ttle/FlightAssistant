package net.torocraft.flighthud.alerts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.AlertSoundInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAlert {
    @Nullable
    public AlertSoundInstance soundInstance;
    public boolean dismissed = false;
    public boolean hidden = false;

    public abstract boolean isTriggered();

    @NotNull
    public abstract AlertSoundData getAlertSoundData();

    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        return false;
    }

    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return 0;
    }
}
