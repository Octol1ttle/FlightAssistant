package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;



public class StallAlert extends AbstractAlert {
    private final StallComputer stall;

    public StallAlert(StallComputer stall) {
        this.stall = stall;
    }

    @Override
    public boolean isTriggered() {
        return stall.status == StallComputer.StallStatus.STALL;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.STALL;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        HudComponent.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.stall"), x, y, FAConfig.hud().warningColor, highlight);

        return true;
    }
}
