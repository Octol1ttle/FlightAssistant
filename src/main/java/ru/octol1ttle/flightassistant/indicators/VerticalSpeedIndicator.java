package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class VerticalSpeedIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;

    public VerticalSpeedIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showVerticalSpeedReadout) {
            return;
        }

        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.75f - 7);
        drawText(textRenderer, context,
                Text.translatable("flightassistant.vertical_speed_short", ": %.2f".formatted(data.velocity.y)),
                x, dim.bFrame, data.velocity.y <= -10.0f ? FAConfig.indicator().warningColor : FAConfig.indicator().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.75f - 7);

        drawText(textRenderer, context, Text.translatable("flightassistant.vertical_speed_short", ""), x, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "vertical_speed";
    }
}

