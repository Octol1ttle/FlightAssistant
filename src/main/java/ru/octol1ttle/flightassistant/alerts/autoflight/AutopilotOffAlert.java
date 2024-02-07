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
        return autoflight.apDisconnectionForced ? AlertSoundData.AUTOPILOT_FORCED_OFF : AlertSoundData.AUTOPILOT_DISCONNECTED_BY_PLAYER;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        if (autoflight.apDisconnectionForced) {
            return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.auto_pilot_off"), x, y,
                    FAConfig.hud().warningTextColor,
                    highlight && !dismissed);
        }

        return 0;
    }

    @Override
    public boolean canBeDismissed(boolean isPlaying) {
        return isPlaying;
    }

    @Override
    public boolean canBeHidden() {
        return autoflight.apDisconnectionForced;
    }
}
