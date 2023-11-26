package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class StallAlert extends AbstractAlert {
    private final StallComputer stall;
    private final AirDataComputer data;

    public StallAlert(StallComputer stall, AirDataComputer data) {
        this.stall = stall;
        this.data = data;
    }

    @Override
    public boolean isTriggered() {
        return stall.anyStall();
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.STALL;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        Text text = Text.translatable("alerts.flightassistant.stall");
        float startX = (width - textRenderer.getWidth(text)) * 0.5f;
        HudComponent.drawHighlightedFont(textRenderer, context, text, startX, y,
                stall.status == StallComputer.StallStatus.FULL_STALL ? CONFIG.alertColor : CONFIG.amberColor,
                highlight);

        return true;
    }
}
