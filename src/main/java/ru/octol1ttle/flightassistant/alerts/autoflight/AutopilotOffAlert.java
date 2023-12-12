package ru.octol1ttle.flightassistant.alerts.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

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
        return autoflight.disconnectionForced ? AlertSoundData.AUTOPILOT_FORCED_OFF : AlertSoundData.AUTOPILOT_DISCONNECTED_BY_PLAYER;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        if (autoflight.disconnectionForced) {
            return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.auto_pilot_off"), x, y,
                    CONFIG.alertColor,
                    highlight && !dismissed);
        }

        return 0;
    }
}
