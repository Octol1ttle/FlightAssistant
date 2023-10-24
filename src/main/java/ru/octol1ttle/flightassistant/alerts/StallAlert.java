package ru.octol1ttle.flightassistant.alerts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.FlightComputer;
import ru.octol1ttle.flightassistant.computers.GPWSComputer;
import ru.octol1ttle.flightassistant.computers.StallComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class StallAlert extends AbstractAlert {
    private static final AlertSoundData STALL = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:stall")),
            0,
            0.75f,
            true
    );
    private final FlightComputer computer;

    public StallAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.stall.stalling >= StallComputer.STATUS_APPROACHING_STALL;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return STALL;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        Text text = Text.translatable("alerts.flightassistant.stall");
        float startX = (width - textRenderer.getWidth(text)) * 0.5f;
        HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                -computer.velocityPerSecond.y >= GPWSComputer.MAX_SAFE_SINK_RATE ?
                        CONFIG.alertColor :
                        CONFIG.amberColor, highlight);

        return true;
    }
}
