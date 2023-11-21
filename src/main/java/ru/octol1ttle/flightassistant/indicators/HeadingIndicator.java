package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;

public class HeadingIndicator extends HudComponent {

    private final Dimensions dim;
    private final AirDataComputer data;

    public HeadingIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        float left = dim.lFrame;
        float right = dim.rFrame;
        float top = dim.tFrame - 10;

        float yText = top - 7;
        float northOffset = data.heading * dim.degreesPerPixel;
        float xNorth = dim.xMid - northOffset;

        if (CONFIG.heading_showReadout) {
            drawFont(textRenderer, context, String.format("%03d", i(data.heading)), dim.xMid - 8, yText, CONFIG.color);
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

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawFont(textRenderer, context, Text.translatable("flightassistant.heading_short"), dim.xMid - 8, dim.tFrame - 17, CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "heading";
    }

    private Text headingToDirection(int degrees) {
        return switch (degrees) {
            case 0, 360 -> Text.translatable("flightassistant.north_short");
            case 90 -> Text.translatable("flightassistant.east_short");
            case 180 -> Text.translatable("flightassistant.south_short");
            case 270 -> Text.translatable("flightassistant.west_short");
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
