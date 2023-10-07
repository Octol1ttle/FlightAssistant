package net.torocraft.flighthud.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.IAlert;
import net.torocraft.flighthud.computers.FlightComputer;
import net.torocraft.flighthud.computers.GPWSComputer;
import org.jetbrains.annotations.NotNull;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class UnsafeSinkrateAlert implements IAlert {
    public static final float SINKRATE_THRESHOLD = 7.5f;
    public static final AlertSoundData SINKRATE = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:sinkrate")),
            1,
            0.5f,
            false
    );
    public static final float PULL_UP_THRESHOLD = 5.0f;
    public static final AlertSoundData PULL_UP = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:pull_up")),
            1,
            0.5f,
            false
    );
    private final FlightComputer computer;

    public UnsafeSinkrateAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.gpws.impactTime >= GPWSComputer.PITCH_CORRECT_THRESHOLD;
    }

    @Override
    public void renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        if (computer.gpws.impactTime <= PULL_UP_THRESHOLD) {
            Text text = Text.translatable("alerts.flighthud.pull_up");
            float startX = (width - textRenderer.getWidth(text)) / 2;
            HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                    CONFIG.white, CONFIG.alertColor, highlight);
            return;
        }

        if (computer.gpws.impactTime <= SINKRATE_THRESHOLD) {
            Text text = Text.translatable("alerts.flighthud.sinkrate");
            float startX = (width - textRenderer.getWidth(text)) / 2;
            HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                    CONFIG.white, CONFIG.amberColor, highlight);
        }
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        if (computer.gpws.impactTime <= PULL_UP_THRESHOLD) {
            return PULL_UP;
        }
        if (computer.gpws.impactTime <= SINKRATE_THRESHOLD) {
            return SINKRATE;
        }

        return AlertSoundData.EMPTY;
    }
}
