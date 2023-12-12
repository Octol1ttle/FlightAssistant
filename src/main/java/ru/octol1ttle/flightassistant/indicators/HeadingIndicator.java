package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;

public class HeadingIndicator extends HudComponent {

    private final Dimensions dim;
    private final AirDataComputer data;
    private final AutoFlightComputer autoflight;

    public HeadingIndicator(Dimensions dim, AirDataComputer data, AutoFlightComputer autoflight) {
        this.dim = dim;
        this.data = data;
        this.autoflight = autoflight;
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
            int color = getHeadingColor(data.heading);
            drawFont(textRenderer, context, String.format("%03d", i(data.heading)), dim.xMid - 8, yText, color);
            drawBox(context, dim.xMid - 15, yText - 1.5f, 30, color);
        }

        if (CONFIG.heading_showScale) {
            drawPointer(context, dim.xMid, top + 10, 0);

            for (int i = -540; i < 540; i++) {
                float x = (i * dim.degreesPerPixel) + xNorth;
                if (x < left) {
                    continue;
                }
                if (x > right) {
                    break;
                }

                int degrees = wrapHeading(i);
                int color = getHeadingColor(degrees);
                double targetHeading = autoflight.getTargetHeading() != null ? autoflight.getTargetHeading() : Integer.MIN_VALUE + 1;

                boolean forceMark = degrees == Math.round(targetHeading);
                boolean enoughSpace = Math.abs(targetHeading - degrees) >= 5;

                if (forceMark || i % 15 == 0 && enoughSpace) {
                    if (i % 90 == 0) {
                        drawFont(textRenderer, context, headingToDirection(degrees), x - 2, yText + 10, color);
                        drawFont(textRenderer, context, headingToAxis(degrees), x - 8, yText + 20, color);
                    } else {
                        drawVerticalLine(context, x, top + 3, top + 10, color);
                    }

                    if (!CONFIG.heading_showReadout || x <= dim.xMid - 26 || x >= dim.xMid + 26) {
                        drawFont(textRenderer, context, String.format("%03d", degrees), x - 8, yText, color);
                    }
                    continue;
                }

                if (i % 5 == 0 && enoughSpace) {
                    drawVerticalLine(context, x, top + 6, top + 10, color);
                }
            }
        }
    }

    private int getHeadingColor(float heading) {
        Float targetHeading = autoflight.getTargetHeading();
        if (targetHeading != null && Math.abs(targetHeading - heading) <= 5.0f) {
            return CONFIG.adviceColor;
        } else {
            return CONFIG.color;
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
