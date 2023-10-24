package ru.octol1ttle.flightassistant.alerts.autoflight;

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

public class ATHRSpeedNotSetAlert extends AbstractAlert {

    private final FlightComputer computer;

    public ATHRSpeedNotSetAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.autoflight.autoThrustEnabled && computer.autoflight.targetSpeed == null;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, x, y,
                Text.translatable("alerts.flightassistant.autoflight.athr_speed_not_set"),
                CONFIG.amberColor,
                !dismissed);
    }
}
