package ru.octol1ttle.flightassistant.alerts.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class AutopilotOffAlert extends AbstractAlert {
    private final AutoFlightComputer autoflight;

    public AutopilotOffAlert(AutoFlightComputer autoflight) {
        this.autoflight = autoflight;
    }

    @Override
    public boolean isTriggered() {
        return !autoflight.autoPilotEnabled;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.AUTOPILOT_DISCONNECT;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.auto_pilot_off"), x, y,
                FAConfig.hud().warningColor,
                highlight && !dismissed && autoflight.apDisconnectionForced);
    }
}
