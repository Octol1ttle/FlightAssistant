package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.computers.navigation.FlightPlanner;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class AltitudeIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;
    private final AutoFlightComputer autoflight;
    private final FlightPlanner plan;

    public AltitudeIndicator(Dimensions dim, AirDataComputer data, AutoFlightComputer autoflight, FlightPlanner plan) {
        this.dim = dim;
        this.data = data;
        this.autoflight = autoflight;
        this.plan = plan;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (data.altitude < -130 || data.altitude > 1300) {
            renderFaulted(context, textRenderer);
            return;
        }

        int top = dim.tFrame;
        int bottom = dim.bFrame;

        int right = dim.rFrame + 2;
        int left = dim.rFrame;

        int blocksPerPixel = 1;

        int floorOffset = Math.round(data.altitude * blocksPerPixel);
        int yFloor = dim.yMid - floorOffset;
        int xAltText = right + 5;

        int safeLevel = data.groundLevel == data.voidLevel ? data.voidLevel + 16 : data.groundLevel;

        if (FAConfig.hud().showAltitudeReadout) {
            Color color = getAltitudeColor(safeLevel, data.altitude);
            drawText(textRenderer, context, asText("%.0f", data.altitude), xAltText, dim.yMid - 3, color);
            drawBorder(context, xAltText - 2, dim.yMid - 5, 28, color);
        }

        if (FAConfig.hud().showGroundAltitude) {
            Color color = data.altitude < safeLevel ? FAConfig.hud().warningColor : FAConfig.hud().frameColor;
            drawText(textRenderer, context, Text.translatable(data.groundLevel == data.voidLevel ? "flightassistant.void_level" : "flightassistant.ground_level"), xAltText - 10, bottom, color);
            drawText(textRenderer, context, asText("%d", MathHelper.floor(data.heightAboveGround)), xAltText, bottom, color);
            drawBorder(context, xAltText - 2, bottom - 2, 28, color);
        }

        if (FAConfig.hud().showAltitudeScale) {
            for (int i = -130; i < 1300; i++) {
                int y = (dim.hScreen - i * blocksPerPixel) - yFloor;
                if (y > (bottom - 5) || i < data.groundLevel) {
                    continue;
                }
                if (y < top) {
                    break;
                }

                Color color = getAltitudeColor(safeLevel, i);
                int targetAltitude = autoflight.getTargetAltitude() != null ? autoflight.getTargetAltitude() : Integer.MIN_VALUE + 1;

                boolean forceMark = i == data.groundLevel || i == targetAltitude;
                boolean enoughSpace = Math.abs(data.groundLevel - i) >= 10 && Math.abs(targetAltitude - i) >= 10;

                if (forceMark || i % 50 == 0 && enoughSpace) {
                    drawHorizontalLine(context, left, right + 2, y, color);
                    if (!FAConfig.hud().showAltitudeReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
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

    private Color getAltitudeColor(int safeLevel, float altitude) {
        if (altitude <= safeLevel) {
            return FAConfig.hud().warningColor;
        }

        Integer minimums = plan.getMinimums(data.groundLevel);
        if (minimums != null && altitude <= minimums) {
            return FAConfig.hud().cautionColor;
        }

        Integer targetAltitude = autoflight.getTargetAltitude();
        if (targetAltitude != null && Math.abs(targetAltitude - altitude) <= 5.0f) {
            return FAConfig.hud().advisoryColor;
        } else {
            return FAConfig.hud().frameColor;
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawText(textRenderer, context, Text.translatable("flightassistant.altitude_short"), dim.rFrame + 7, dim.yMid - 3, FAConfig.hud().warningColor);
    }

    @Override
    public String getId() {
        return "altitude";
    }
}
