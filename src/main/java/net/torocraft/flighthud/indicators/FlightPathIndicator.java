package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.computers.FlightComputer;

public class FlightPathIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public FlightPathIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!CONFIG.flightPath_show) {
            return;
        }

        float deltaPitch = computer.pitch - computer.flightPitch;
        float deltaHeading = computer.flightHeading - computer.heading;

        if (deltaHeading < -180) {
            deltaHeading += 360;
        }

        float y = dim.yMid;
        float x = dim.xMid;

        y += i(deltaPitch * dim.degreesPerPixel);
        x += i(deltaHeading * dim.degreesPerPixel);

        if (y < dim.tFrame || y > dim.bFrame || x < dim.lFrame || x > dim.rFrame) {
            return;
        }

        float l = x - 3;
        float r = x + 3;
        float t = y - 3 - CONFIG.halfThickness;
        float b = y + 3 - CONFIG.halfThickness;

        int color = computer.gpws.getGPWSLampColor();
        drawVerticalLine(context, l, t, b, color);
        drawVerticalLine(context, r, t, b, color);

        drawHorizontalLine(context, l, r, t, color);
        drawHorizontalLine(context, l, r, b, color);

        drawVerticalLine(context, x, t - 5, t, color);
        drawHorizontalLine(context, l - 4, l, y - CONFIG.halfThickness, color);
        drawHorizontalLine(context, r, r + 4, y - CONFIG.halfThickness, color);
    }
}
