package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.computers.FlightComputer;

public class AltitudeIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public AltitudeIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float top = dim.tFrame;
        float bottom = dim.bFrame;

        float right = dim.rFrame + 2;
        float left = dim.rFrame;

        float blocksPerPixel = 1;

        float floorOffset = i(computer.altitude * blocksPerPixel);
        float yFloor = dim.yMid - floorOffset;
        float xAltText = right + 5;

        if (CONFIG.altitude_showGroundInfo) {
            drawHeightIndicator(context, left - 1, dim.yMid, bottom - dim.yMid, CONFIG.color);
        }

        if (CONFIG.altitude_showReadout) {
            int color = computer.altitude < computer.groundLevel ? CONFIG.alertColor : CONFIG.color;
            drawFont(textRenderer, context, String.format("%.0f", computer.altitude), xAltText, dim.yMid - 3, color);
            drawBox(context, xAltText - 2, dim.yMid - 4.5f, 28, color);
        }

        if (CONFIG.altitude_showHeight) {
            drawFont(textRenderer, context, Text.translatable("flighthud.ground_level"), xAltText - 10, bottom + 3, CONFIG.color);
            String heightText = String.format("%d", i(computer.distanceFromGround));
            drawFont(textRenderer, context, heightText, xAltText, bottom + 3, CONFIG.color);
            drawBox(context, xAltText - 2, bottom + 1.5f, 28, CONFIG.color);
        }

        if (CONFIG.altitude_showScale) {
            for (int i = -100; i < 1000; i += 10) {
                float y = (dim.hScreen - i * blocksPerPixel) - yFloor;
                if (y < top || y > (bottom - 5))
                    continue;

                int color = i <= computer.groundLevel ? CONFIG.alertColor : CONFIG.color;
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
        float blocksPerPixel = h / (computer.worldHeight + 64.0f);
        float yAlt = bottom - i((computer.altitude + 64) * blocksPerPixel);
        float yFloor = bottom - i(64 * blocksPerPixel);

        drawVerticalLine(context, x, top - 1, bottom + 1, color);

        float yGroundLevel = bottom - (computer.groundLevel + 64f) * blocksPerPixel;
        fill(context, x - 3, yGroundLevel + 2, x, yFloor, color);

        drawHorizontalLine(context, x - 6, x - 1, top, color);
        drawHorizontalLine(context, x - 6, x - 1, yFloor, color);
        drawHorizontalLine(context, x - 6, x - 1, bottom, color);

        drawPointer(context, x, yAlt, 90);
    }

}
