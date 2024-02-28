package ru.octol1ttle.flightassistant.alerts.fault;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.HudRenderer;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.config.FAConfig;



public class IndicatorFaultAlert extends AbstractAlert {

    private final HudRenderer renderer;

    public IndicatorFaultAlert(HudRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean isTriggered() {
        return !renderer.faulted.isEmpty();
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, int x, int y, boolean highlight) {
        int i = 0;
        for (HudComponent component : renderer.faulted) {
            i += HudComponent.drawText(textRenderer, context, Text.translatable("alerts.flightassistant.fault.indicators." + component.getId()), x, y,
                    FAConfig.indicator().cautionColor);
            y += 10;
        }

        return i;
    }
}
