package ru.octol1ttle.flightassistant.alerts.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.BaseAlert;
import ru.octol1ttle.flightassistant.alerts.IECAMAlert;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class AutopilotOffAlert extends BaseAlert implements IECAMAlert {
    private final AutoFlightComputer autoflight;

    public AutopilotOffAlert(AutoFlightComputer autoflight) {
        this.autoflight = autoflight;
    }

    @Override
    public boolean isTriggered() {
        return !autoflight.autoPilotEnabled;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return AlertSoundData.AUTOPILOT_DISCONNECT;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.autopilot_off"), x, y,
                FAConfig.indicator().warningColor,
                highlight && autoflight.apDisconnectionForced);
    }
}
