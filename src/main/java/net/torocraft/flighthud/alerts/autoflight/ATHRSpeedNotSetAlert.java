package net.torocraft.flighthud.alerts.autoflight;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.AbstractAlert;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.ECAMSoundData;
import net.torocraft.flighthud.computers.FlightComputer;
import org.jetbrains.annotations.NotNull;

import static net.torocraft.flighthud.HudComponent.CONFIG;

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
                Text.translatable("alerts.flighthud.autoflight.athr_speed_not_set"),
                CONFIG.amberColor,
                !dismissed);
    }
}
