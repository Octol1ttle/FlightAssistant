package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class ElytraHealthIndicator extends HudComponent {

    private final Dimensions dim;
    private final AirDataComputer data;

    public ElytraHealthIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        int x = dim.xMid;
        int y = dim.bFrame;

        if (FAConfig.indicator().showElytraHealth && data.elytraHealth != null) {
            Color color;
            if (data.elytraHealth <= 5.0f) { // TODO: configurable
                color = FAConfig.indicator().warningColor;
            } else {
                color = data.elytraHealth <= 10.0f ? FAConfig.indicator().cautionColor : FAConfig.indicator().frameColor;
            }
            drawBorder(context, x - 3, y - 2, 30, color);
            drawText(textRenderer, context, Text.translatable("flightassistant.elytra_short"), x - 10, y, color);

            drawText(textRenderer, context, asText("%d", MathHelper.ceil(data.elytraHealth)).append("%"), x, y, color);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawText(textRenderer, context, Text.translatable("flightassistant.elytra_health_short"),
                dim.xMid, dim.bFrame,
                FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "elytra_health";
    }
}
