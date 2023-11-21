package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;

public class SpeedIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;

    public SpeedIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float top = dim.tFrame;
        float bottom = dim.bFrame;

        float left = dim.lFrame - 2;
        float right = dim.lFrame;
        float unitPerPixel = 30;

        float floorOffset = data.speed * unitPerPixel;
        float yFloor = dim.yMid - floorOffset;

        float xSpeedText = left - 5;

        if (CONFIG.speed_showReadout) {
            drawRightAlignedFont(textRenderer, context, Text.literal(String.format("%.2f", data.speed)), xSpeedText, dim.yMid - 3, CONFIG.color);
            drawBox(context, xSpeedText - 29.5f, dim.yMid - 4.5f, 30, CONFIG.color);

            float frameWidth = dim.rFrame - dim.lFrame;
            drawFont(textRenderer, context, Text.translatable("flightassistant.ground_speed_short", String.format("%.2f", data.velocityPerSecond.horizontalLength())), dim.lFrame + frameWidth * 0.25f, dim.hScreen * 0.8f, CONFIG.color);
            drawFont(textRenderer, context, Text.translatable("flightassistant.vertical_speed_short", String.format("%.2f", data.velocityPerSecond.y)), dim.lFrame + frameWidth * 0.75f - 7, dim.hScreen * 0.8f, data.velocityPerSecond.y <= -10.0f ? CONFIG.alertColor : CONFIG.color);
        }


        if (CONFIG.speed_showScale) {
            for (float i = 0; i <= 100; i += 0.25f) {
                float y = dim.hScreen - i * unitPerPixel - yFloor;
                if (y < top || y > (bottom - 5))
                    continue;

                if (i % 1 == 0) {
                    drawHorizontalLine(context, left - 2, right, y, CONFIG.color);
                    if (!CONFIG.speed_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        drawRightAlignedFont(textRenderer, context, Text.literal(String.format("%.0f", i)), xSpeedText, y - 3, CONFIG.color);
                    }
                }
                drawHorizontalLine(context, left, right, y, CONFIG.color);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawRightAlignedFont(textRenderer, context, Text.translatable("flightassistant.speed_short"), dim.lFrame - 7, dim.yMid - 3, CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "speed";
    }
}
