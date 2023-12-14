package ru.octol1ttle.flightassistant.alerts.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class NoFireworksInHotbarAlert extends AbstractAlert {
    private final FireworkController firework;

    public NoFireworksInHotbarAlert(FireworkController firework) {
        this.firework = firework;
    }

    @Override
    public boolean isTriggered() {
        return firework.noFireworks;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.firework.no_fireworks"), x, y,
                CONFIG.amberColor,
                !dismissed);
    }
}