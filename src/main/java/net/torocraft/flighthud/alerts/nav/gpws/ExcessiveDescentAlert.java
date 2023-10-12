package net.torocraft.flighthud.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.AbstractAlert;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.GPWSSoundData;
import net.torocraft.flighthud.computers.FlightComputer;
import org.jetbrains.annotations.NotNull;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class ExcessiveDescentAlert extends AbstractAlert {
    private static final float SINK_RATE_THRESHOLD = 7.5f;
    private static final AlertSoundData SINK_RATE = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:sink_rate")),
            2,
            0.75f,
            false
    );
    private static final float PULL_UP_THRESHOLD = 5.0f;
    private final FlightComputer computer;

    public ExcessiveDescentAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.gpws.descentImpactTime >= 0.0f;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        if (computer.gpws.descentImpactTime <= PULL_UP_THRESHOLD) {
            Text text = Text.translatable("alerts.flighthud.pull_up");
            float startX = (width - textRenderer.getWidth(text)) * 0.5f;
            HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                    CONFIG.alertColor, highlight);

            return true;
        }

        if (computer.gpws.descentImpactTime <= SINK_RATE_THRESHOLD) {
            Text text = Text.translatable("alerts.flighthud.sink_rate");
            float startX = (width - textRenderer.getWidth(text)) * 0.5f;
            HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                    CONFIG.amberColor, highlight);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        if (computer.gpws.descentImpactTime <= PULL_UP_THRESHOLD) {
            return GPWSSoundData.PULL_UP;
        }
        if (computer.gpws.descentImpactTime <= SINK_RATE_THRESHOLD) {
            return SINK_RATE;
        }

        return AlertSoundData.EMPTY;
    }
}
