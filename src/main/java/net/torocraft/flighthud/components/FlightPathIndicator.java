package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

import static net.torocraft.flighthud.FlightSafetyMonitor.gpwsLampColor;

public class FlightPathIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public FlightPathIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client) {
        if (!CONFIG.flightPath_show) {
            return;
        }

        float deltaPitch = computer.pitch - computer.flightPitch;
        float deltaHeading = wrapHeading(computer.flightHeading) - wrapHeading(computer.heading);

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

        drawVerticalLine(context, l, t, b, gpwsLampColor);
        drawVerticalLine(context, r, t, b, gpwsLampColor);

        drawHorizontalLine(context, l, r, t, gpwsLampColor);
        drawHorizontalLine(context, l, r, b, gpwsLampColor);

        drawVerticalLine(context, x, t - 5, t, gpwsLampColor);
        drawHorizontalLine(context, l - 4, l, y - CONFIG.halfThickness, gpwsLampColor);
        drawHorizontalLine(context, r, r + 4, y - CONFIG.halfThickness, gpwsLampColor);
    }
}
