package net.torocraft.flighthud.alerts;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.computers.FlightComputer;
import net.torocraft.flighthud.computers.GPWSComputer;
import net.torocraft.flighthud.computers.StallComputer;
import org.jetbrains.annotations.NotNull;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class StallAlert implements IAlert {
    private static final AlertSoundData STALL = new AlertSoundData(
            SoundEvent.of(new Identifier("flighthud:stall")),
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
        Text text = Text.translatable("alerts.flighthud.stall");
        float startX = (width - textRenderer.getWidth(text)) * 0.5f;
        HudComponent.drawHighlightedFont(textRenderer, context, startX, y, text,
                -computer.velocityPerSecond.y >= GPWSComputer.MAX_SAFE_SINK_RATE ?
                        CONFIG.alertColor :
                        CONFIG.amberColor, highlight);

        return true;
    }
}
