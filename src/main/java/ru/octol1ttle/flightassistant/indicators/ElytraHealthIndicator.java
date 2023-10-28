package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.FlightComputer;

public class ElytraHealthIndicator extends HudComponent {

    private final Dimensions dim;
    private final FlightComputer computer;

    public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float x = dim.wScreen * CONFIG.elytra_x;
        float y = dim.hScreen * CONFIG.elytra_y;

        if (CONFIG.elytra_showHealth && computer.elytraHealth != null) {
            int color;
            if (computer.elytraHealth <= 5.0f) {
                color = CONFIG.alertColor;
            } else {
                color = computer.elytraHealth <= 10.0f ? CONFIG.amberColor : CONFIG.color;
            }
            drawBox(context, x - 3.5f, y - 1.5f, 30, color);
            drawFont(textRenderer, context, Text.translatable("flightassistant.elytra_short"), x - 10, y, color);
            drawFont(textRenderer, context, String.format("%d", i(computer.elytraHealth)) + "%", x, y, color);
        }
    }
}
