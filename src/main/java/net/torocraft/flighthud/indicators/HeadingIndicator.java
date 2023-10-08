package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.computers.FlightComputer;

public class HeadingIndicator extends HudComponent {

    private final Dimensions dim;
    private final FlightComputer computer;

    public HeadingIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float left = dim.lFrame;
        float right = dim.rFrame;
        float top = dim.tFrame - 10;

        float yText = top - 7;
        float northOffset = computer.heading * dim.degreesPerPixel;
        float xNorth = dim.xMid - northOffset;

        if (CONFIG.heading_showReadout) {
            drawFont(textRenderer, context, String.format("%03d", i(computer.heading)), dim.xMid - 8, yText, CONFIG.color);
            drawBox(context, dim.xMid - 15, yText - 1.5f, 30, CONFIG.color);
        }

        if (CONFIG.heading_showScale) {
            drawPointer(context, dim.xMid, top + 10, 0);
            for (int i = -540; i < 540; i = i + 5) {
                float x = (i * dim.degreesPerPixel) + xNorth;
                if (x < left || x > right)
                    continue;

                if (i % 15 == 0) {
                    if (i % 90 == 0) {
                        drawFont(textRenderer, context, headingToDirection(i), x - 2, yText + 10, CONFIG.color);
                        drawFont(textRenderer, context, headingToAxis(i), x - 8, yText + 20, CONFIG.color);
                    } else {
                        drawVerticalLine(context, x, top + 3, top + 10, CONFIG.color);
                    }

                    if (!CONFIG.heading_showReadout || x <= dim.xMid - 26 || x >= dim.xMid + 26) {
                        drawFont(textRenderer, context, String.format("%03d", i(i)), x - 8, yText, CONFIG.color);
                    }
                } else {
                    drawVerticalLine(context, x, top + 6, top + 10, CONFIG.color);
                }
            }
        }
    }

    private String headingToDirection(int degrees) {
        return switch (i(degrees)) {
            case 0, 360 -> "N";
            case 90 -> "E";
            case 180 -> "S";
            case 270 -> "W";
            default -> "";
        };
    }

    private String headingToAxis(int degrees) {
        return switch (i(degrees)) {
            case 0, 360 -> "-Z";
            case 90 -> "+X";
            case 180 -> "+Z";
            case 270 -> "-X";
            default -> "";
        };
    }

}
