package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

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

        if (FAConfig.hud().showSpeedReadout) {
            drawRightAlignedText(textRenderer, context, asText("%.2f", data.speed), xSpeedText, dim.yMid - 3, FAConfig.hud().frameColor);
            drawBox(context, xSpeedText - 29.5f, dim.yMid - 4.5f, 30, FAConfig.hud().frameColor);

            float frameWidth = dim.rFrame - dim.lFrame;
            if (FAConfig.hud().showGroundSpeedReadout) {
                drawText(textRenderer, context, Text.translatable("flightassistant.ground_speed_short", String.format("%.2f", data.velocityPerSecond.horizontalLength())), dim.lFrame + frameWidth * 0.25f, dim.bFrame, FAConfig.hud().frameColor);
            }
            if (FAConfig.hud().showVerticalSpeedReadout) {
                drawText(textRenderer, context, Text.translatable("flightassistant.vertical_speed_short", String.format("%.2f", data.velocityPerSecond.y)), dim.lFrame + frameWidth * 0.75f - 7, dim.bFrame, data.velocityPerSecond.y <= -10.0f ? FAConfig.hud().warningColor : FAConfig.hud().frameColor);
            }
        }

        if (FAConfig.hud().showSpeedScale) {
            for (float i = 0; i <= 100; i += 0.25f) {
                float y = dim.hScreen - i * unitPerPixel - yFloor;
                if (y < top || y > (bottom - 5))
                    continue;

                if (i % 1 == 0) {
                    drawHorizontalLine(context, left - 2, right, y, FAConfig.hud().frameColor);
                    if (!FAConfig.hud().showSpeedReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        drawRightAlignedText(textRenderer, context, asText("%.0f", i), xSpeedText, y - 3, FAConfig.hud().frameColor);
                    }
                }
                drawHorizontalLine(context, left, right, y, FAConfig.hud().frameColor);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawRightAlignedText(textRenderer, context, Text.translatable("flightassistant.speed_short"), dim.lFrame - 7, dim.yMid - 3, FAConfig.hud().warningColor);
    }

    @Override
    public String getId() {
        return "speed";
    }
}
