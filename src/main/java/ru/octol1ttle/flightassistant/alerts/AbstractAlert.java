package ru.octol1ttle.flightassistant.alerts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.octol1ttle.flightassistant.AlertSoundInstance;

public abstract class AbstractAlert {
    @Nullable
    public AlertSoundInstance soundInstance;
    public boolean played = false;
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

    public boolean canBeDismissed(boolean isPlaying) {
        return true;
    }

    public boolean canBeHidden() {
        return true;
    }
}
