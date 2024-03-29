package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class GroundSpeedIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;

    public GroundSpeedIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.indicator().showGroundSpeedReadout) {
            return;
        }

        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.25f);
        drawText(textRenderer, context,
                Text.translatable("flightassistant.ground_speed_short", ": %.2f".formatted(data.velocity.horizontalLength())),
                x, dim.bFrame, FAConfig.indicator().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        int frameWidth = dim.rFrame - dim.lFrame;
        int x = MathHelper.floor(dim.lFrame + frameWidth * 0.25f);

        drawText(textRenderer, context, Text.translatable("flightassistant.ground_speed_short", ""), x, dim.bFrame, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "ground_speed";
    }
}
