package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.FlightComputer;

public class FlightModeIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public FlightModeIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float x = dim.lFrame + dim.wFrame * 0.2f;
        float y = dim.bFrame + 10;
        if (computer.firework.lastTogaLock != null && computer.time.prevMillis - computer.firework.lastTogaLock < 1000) {
            Text text = Text.translatable("flightassistant.toga_lock");
            drawHighlightedFont(textRenderer, context,
                    text,
                    x - textRenderer.getWidth(text) * 0.5f, y,
                    CONFIG.amberColor, computer.time.highlight);
            return;
        }

        if (computer.autoflight.autoThrustEnabled) {
            if (computer.autoflight.targetSpeed == null) {
                Text text = Text.translatable("flightassistant.thrust_mode_speed_not_selected");
                drawHighlightedFont(textRenderer, context, text,
                        x - textRenderer.getWidth(text) * 0.5f, y,
                        CONFIG.amberColor, computer.time.highlight);
            } else {
                Text text = Text.translatable("flightassistant.thrust_mode_speed", computer.autoflight.targetSpeed);
                drawFont(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, CONFIG.white);
            }
        }
    }
}