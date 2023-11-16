package ru.octol1ttle.flightassistant.alerts.other;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.safety.StallComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class StallAlert extends AbstractAlert {
    private static final AlertSoundData STALL = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:stall")),
            0,
            0.75f,
            true
    );
    private final StallComputer stall;
    private final AirDataComputer data;

    public StallAlert(StallComputer stall, AirDataComputer data) {
        this.stall = stall;
        this.data = data;
    }

    @Override
    public boolean isTriggered() {
        return stall.stalling >= StallComputer.STATUS_APPROACHING_STALL;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return STALL;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        Text text = Text.translatable("alerts.flightassistant.stall");
        float startX = (width - textRenderer.getWidth(text)) * 0.5f;
        HudComponent.drawHighlightedFont(textRenderer, context, text, startX, y,
                -data.velocityPerSecond.y >= GPWSComputer.MAX_SAFE_SINK_RATE ?
                        CONFIG.alertColor :
                        CONFIG.amberColor, highlight);

        return true;
    }
}
