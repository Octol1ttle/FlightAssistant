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

public class AutoFireworkOffAlert extends BaseAlert implements IECAMAlert {
    private final AutoFlightComputer autoflight;

    public AutoFireworkOffAlert(AutoFlightComputer autoflight) {
        this.autoflight = autoflight;
    }

    @Override
    public boolean isTriggered() {
        return !autoflight.autoFireworkEnabled;
    }

    @Override
    public @NotNull AlertSoundData getSoundData() {
        return autoflight.afrwkDisconnectionForced ? AlertSoundData.MASTER_CAUTION : AlertSoundData.EMPTY;
    }

    @Override
    public int render(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.autoflight.auto_firework_off"), x, y,
                FAConfig.indicator().cautionColor,
                highlight && autoflight.afrwkDisconnectionForced);
    }
}
