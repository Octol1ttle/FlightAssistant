package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;

public class AltitudeIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;

    public AltitudeIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float top = dim.tFrame;
        float bottom = dim.bFrame;

        float right = dim.rFrame + 2;
        float left = dim.rFrame;

        float blocksPerPixel = 1;

        float floorOffset = i(data.altitude * blocksPerPixel);
        float yFloor = dim.yMid - floorOffset;
        float xAltText = right + 5;

        int safeLevel = data.groundLevel == data.voidLevel ? data.voidLevel + 16 : data.groundLevel;
        boolean isAltitudeSafe = data.altitude >= safeLevel;

        if (CONFIG.altitude_showGroundInfo) {
            drawHeightIndicator(context, left - 1, dim.yMid, bottom - dim.yMid, CONFIG.color);
        }

        if (CONFIG.altitude_showReadout) {
            int color = !isAltitudeSafe ? CONFIG.alertColor : CONFIG.color;
            drawFont(textRenderer, context, String.format("%.0f", data.altitude), xAltText, dim.yMid - 3, color);
            drawBox(context, xAltText - 2, dim.yMid - 4.5f, 28, color);
        }

        if (CONFIG.altitude_showHeight) {
            int color = !isAltitudeSafe ? CONFIG.alertColor : CONFIG.color;
            drawFont(textRenderer, context, Text.translatable(data.groundLevel == data.voidLevel ? "flightassistant.void_level" : "flightassistant.ground_level"), xAltText - 10, bottom + 3, color);
            String heightText = String.format("%d", i(data.distanceFromGround));
            drawFont(textRenderer, context, heightText, xAltText, bottom + 3, color);
            drawBox(context, xAltText - 2, bottom + 1.5f, 28, color);
        }

        if (CONFIG.altitude_showScale) {
            for (int i = -150; i < 1000; i += 10) {
                float y = (dim.hScreen - i * blocksPerPixel) - yFloor;
                if (y < top || y > (bottom - 5))
                    continue;

                int color = i <= safeLevel ? CONFIG.alertColor : CONFIG.color;
                if (i % 50 == 0) {
                    drawHorizontalLine(context, left, right + 2, y, color);
                    if (!CONFIG.altitude_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        drawFont(textRenderer, context, String.format("%d", i), xAltText, y - 3, color);
                    }
                }
                drawHorizontalLine(context, left, right, y, color);
            }
        }
    }

    private void drawHeightIndicator(DrawContext context, float x, float top, float h, int color) {
        float bottom = top + h;
        float blocksPerPixel = h / (data.world.getHeight() + 64.0f);
        float yAlt = bottom - i((data.altitude + 64) * blocksPerPixel);
        float yFloor = bottom - i(64 * blocksPerPixel);

        drawVerticalLine(context, x, top - 1, bottom + 1, color);

        float yGroundLevel = bottom - (data.groundLevel + 64f) * blocksPerPixel;
        fill(context, x - 3, yGroundLevel + 2, x, yFloor, color);

        drawHorizontalLine(context, x - 6, x - 1, top, color);
        drawHorizontalLine(context, x - 6, x - 1, yFloor, color);
        drawHorizontalLine(context, x - 6, x - 1, bottom, color);

        drawPointer(context, x, yAlt, 90);
    }

}