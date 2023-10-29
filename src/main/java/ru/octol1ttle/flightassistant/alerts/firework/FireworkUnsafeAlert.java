package ru.octol1ttle.flightassistant.alerts.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.ECAMSoundData;
import ru.octol1ttle.flightassistant.computers.FlightComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class FireworkUnsafeAlert extends AbstractAlert {

    private final FlightComputer computer;

    public FireworkUnsafeAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.firework.unsafeFireworks;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.firework.unsafe"), x, y,
                CONFIG.alertColor,
                !dismissed && highlight);
    }
}
