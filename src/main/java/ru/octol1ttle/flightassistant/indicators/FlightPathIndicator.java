package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.FAConfig;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

public class FlightPathIndicator extends HudComponent {
    private final Dimensions dim;
    private final AirDataComputer data;
    private final GPWSComputer gpws;

    public FlightPathIndicator(Dimensions dim, AirDataComputer data, GPWSComputer gpws) {
        this.dim = dim;
        this.data = data;
        this.gpws = gpws;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!CONFIG.flightPath_show) {
            return;
        }

        float deltaPitch = data.pitch - data.flightPitch;
        float deltaHeading = data.flightHeading - data.heading;

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
        float t = y - 3 - FAConfig.get().halfThickness;
        float b = y + 3 - FAConfig.get().halfThickness;

        Color color = gpws.getGPWSLampColor();
        drawVerticalLine(context, l, t, b, color);
        drawVerticalLine(context, r, t, b, color);

        drawHorizontalLine(context, l, r, t, color);
        drawHorizontalLine(context, l, r, b, color);

        drawVerticalLine(context, x, t - 5, t, color);
        drawHorizontalLine(context, l - 4, l, y - FAConfig.get().halfThickness, color);
        drawHorizontalLine(context, r, r + 4, y - FAConfig.get().halfThickness, color);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.flight_path_short"), dim.xMid, dim.yMid + 10, FAConfig.get().alertColor);
    }

    @Override
    public String getId() {
        return "flight_path";
    }
}
