package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;

public class ElytraHealthIndicator extends HudComponent {

    private final Dimensions dim;
    private final AirDataComputer data;

    public ElytraHealthIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float x = dim.wScreen * CONFIG.elytra_x;
        float y = dim.hScreen * CONFIG.elytra_y;

        if (CONFIG.elytra_showHealth && data.elytraHealth != null) {
            int color;
            if (data.elytraHealth <= 5.0f) { // TODO: configurable
                color = CONFIG.alertColor;
            } else {
                color = data.elytraHealth <= 10.0f ? CONFIG.amberColor : CONFIG.color;
            }
            drawBox(context, x - 3.5f, y - 1.5f, 30, color);
            drawFont(textRenderer, context, Text.translatable("flightassistant.elytra_short"), x - 10, y, color);
            drawFont(textRenderer, context, String.format("%d", i(data.elytraHealth)) + "%", x, y, color);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawFont(textRenderer, context, Text.translatable("flightassistant.elytra_health_short"),
                dim.wScreen * CONFIG.elytra_x, dim.hScreen * CONFIG.elytra_y,
                CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "elytra_health";
    }
}
