package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.shims.DrawContext;

import static net.torocraft.flighthud.FlightSafetyMonitor.isStalling;

public class SpeedIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public SpeedIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, MinecraftClient mc) {
        float top = dim.tFrame;
        float bottom = dim.bFrame;

        float left = dim.lFrame - 2;
        float right = dim.lFrame;
        float unitPerPixel = 30;

        float floorOffset = computer.speed * unitPerPixel;
        float yFloor = dim.yMid - floorOffset;

        float xSpeedText = left - 5;

        if (CONFIG.speed_showReadout) {
            int color = isStalling ? CONFIG.alertColor : CONFIG.color;
            drawRightAlignedFont(mc, context, String.format("%.2f", computer.speed), xSpeedText, dim.yMid - 3, color);
            drawBox(context, xSpeedText - 29.5f, dim.yMid - 4.5f, 30, color);

            float frameWidth = dim.rFrame - dim.lFrame;
            drawFont(mc, context, String.format("G/S: %.2f", computer.velocityPerSecond.horizontalLength()), dim.lFrame + frameWidth * 0.25f, dim.hScreen * 0.8f, CONFIG.color);
            drawFont(mc, context, String.format("V/S: %.2f", computer.velocityPerSecond.y), dim.lFrame + frameWidth * 0.75f - 7, dim.hScreen * 0.8f, computer.velocityPerSecond.y <= -10.0f ? CONFIG.alertColor : CONFIG.color);
        }


        if (CONFIG.speed_showScale) {
            for (float i = 0; i <= 100; i = i + 0.25f) {
                float y = dim.hScreen - i * unitPerPixel - yFloor;
                if (y < top || y > (bottom - 5))
                    continue;

                if (i % 1 == 0) {
                    drawHorizontalLine(context, left - 2, right, y, CONFIG.color);
                    if (!CONFIG.speed_showReadout || y > dim.yMid + 7 || y < dim.yMid - 7) {
                        drawRightAlignedFont(mc, context, String.format("%.0f", i), xSpeedText, y - 3, CONFIG.color);
                    }
                }
                drawHorizontalLine(context, left, right, y, CONFIG.color);
            }
        }
    }
}
