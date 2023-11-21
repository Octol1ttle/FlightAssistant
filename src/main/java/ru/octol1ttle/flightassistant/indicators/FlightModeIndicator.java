package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;

public class FlightModeIndicator extends HudComponent {
    private final Dimensions dim;
    private final FireworkController firework;
    private final TimeComputer time;
    private final AutoFlightComputer autoflight;

    public FlightModeIndicator(Dimensions dim, FireworkController firework, TimeComputer time, AutoFlightComputer autoflight) {
        this.dim = dim;
        this.firework = firework;
        this.time = time;
        this.autoflight = autoflight;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float x = dim.lFrame + dim.wFrame * 0.2f;
        float y = dim.bFrame + 10;
        if (firework.lastTogaLock != null && time.prevMillis - firework.lastTogaLock < 2000) {
            Text text = Text.translatable("flightassistant.toga_lock");
            drawHighlightedFont(textRenderer, context,
                    text,
                    x - textRenderer.getWidth(text) * 0.5f, y,
                    CONFIG.amberColor, time.highlight);
            return;
        }

        if (autoflight.autoThrustEnabled) {
            if (autoflight.targetSpeed == null) {
                Text text = Text.translatable("flightassistant.thrust_mode_speed_not_selected");
                drawHighlightedFont(textRenderer, context, text,
                        x - textRenderer.getWidth(text) * 0.5f, y,
                        CONFIG.amberColor, time.highlight);
            } else {
                Text text = Text.translatable("flightassistant.thrust_mode_speed", autoflight.targetSpeed);
                drawFont(textRenderer, context, text, x - textRenderer.getWidth(text) * 0.5f, y, CONFIG.white);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawFont(textRenderer, context, Text.translatable("flightassistant.flight_mode_short"),
                dim.lFrame + dim.wFrame * 0.2f, dim.bFrame + 10,
                CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "flight_mode";
    }
}