package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
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
                int degrees = wrapHeading(i);

                if (i % 15 == 0) {
                    if (i % 90 == 0) {
                        drawFont(textRenderer, context, headingToDirection(degrees), x - 2, yText + 10, CONFIG.color);
                        drawFont(textRenderer, context, headingToAxis(degrees), x - 8, yText + 20, CONFIG.color);
                    } else {
                        drawVerticalLine(context, x, top + 3, top + 10, CONFIG.color);
                    }

                    if (!CONFIG.heading_showReadout || x <= dim.xMid - 26 || x >= dim.xMid + 26) {
                        drawFont(textRenderer, context, String.format("%03d", degrees), x - 8, yText, CONFIG.color);
                    }
                } else {
                    drawVerticalLine(context, x, top + 6, top + 10, CONFIG.color);
                }
            }
        }
    }

    // TODO: maybe convert throws into alternate law triggers and disable the component?
    private Text headingToDirection(int degrees) {
        return switch (degrees) {
            case 0, 360 -> Text.translatable("flighthud.north_short");
            case 90 -> Text.translatable("flighthud.east_short");
            case 180 -> Text.translatable("flighthud.south_short");
            case 270 -> Text.translatable("flighthud.west_short");
            default -> throw new IllegalArgumentException("Degree range out of bounds: " + degrees);
        };
    }

    private String headingToAxis(int degrees) {
        return switch (degrees) {
            case 0, 360 -> "-Z";
            case 90 -> "+X";
            case 180 -> "+Z";
            case 270 -> "-X";
            default -> throw new IllegalArgumentException("Degree range out of bounds: " + degrees);
        };
    }

    private int wrapHeading(int degrees) {
        int i = degrees % 360;
        if (i < 0) {
            i += 360;
        }

        return i;
    }
}
