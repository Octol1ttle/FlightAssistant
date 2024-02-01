package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;

public class AltitudeIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;
    private final AutoFlightComputer autoflight;

    public AltitudeIndicator(Dimensions dim, AirDataComputer data, AutoFlightComputer autoflight) {
        this.dim = dim;
        this.data = data;
        this.autoflight = autoflight;
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

        if (CONFIG.altitude_showReadout) {
            int color = getAltitudeColor(safeLevel, data.altitude);
            drawText(textRenderer, context, asText("%.0f", data.altitude), xAltText, dim.yMid - 3, color);
            drawBox(context, xAltText - 2, dim.yMid - 4.5f, 28, color);
        }

        if (CONFIG.altitude_showHeight) {
            int color = data.altitude < safeLevel ? CONFIG.alertColor : CONFIG.color;
            drawText(textRenderer, context, Text.translatable(data.groundLevel == data.voidLevel ? "flightassistant.void_level" : "flightassistant.ground_level"), xAltText - 10, bottom + 3, color);
            drawText(textRenderer, context, asText("%d", i(data.heightAboveGround)), xAltText, bottom + 3, color);
            drawBox(context, xAltText - 2, bottom + 1.5f, 28, color);
        }

        if (CONFIG.altitude_showScale) {
            for (int i = -150; i < 1000; i++) {
                float y = (dim.hScreen - i * blocksPerPixel) - yFloor;
                if (y > (bottom - 5) || i < data.groundLevel) {
                    continue;
                }
                if (y < top) {
                    break;
                }

                int color = getAltitudeColor(safeLevel, i);
                int targetAltitude = autoflight.getTargetAltitude() != null ? autoflight.getTargetAltitude() : Integer.MIN_VALUE + 1;

                boolean forceMark = i == data.groundLevel || i == targetAltitude;
                boolean enoughSpace = Math.abs(data.groundLevel - i) >= 10 && Math.abs(targetAltitude - i) >= 10;

                if (forceMark || i % 50 == 0 && enoughSpace) {
                    drawHorizontalLine(context, left, right + 2, y, color);
                    if (!CONFIG.altitude_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        drawText(textRenderer, context, asText("%d", i), xAltText, y - 3, color);
                    }
                    continue;
                }

                if (i % 10 == 0 && enoughSpace) {
                    drawHorizontalLine(context, left, right, y, color);
                }
            }
        }
    }

    private int getAltitudeColor(int safeLevel, float altitude) {
        if (altitude <= safeLevel) {
            return CONFIG.alertColor;
        }

        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude != null && Math.abs(targetAltitude - altitude) <= 5.0f) {
            return CONFIG.adviceColor;
        } else {
            return CONFIG.color;
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawText(textRenderer, context, Text.translatable("flightassistant.altitude_short"), dim.rFrame + 7, dim.yMid - 3, CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "altitude";
    }
}
