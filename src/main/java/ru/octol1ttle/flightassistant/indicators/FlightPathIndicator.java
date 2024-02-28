package ru.octol1ttle.flightassistant.indicators;

import java.awt.Color;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

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
        if (!FAConfig.indicator().showFlightPath) {
            return;
        }

        float deltaPitch = data.pitch - data.flightPitch;
        float deltaHeading = data.flightHeading - data.heading;

        if (deltaHeading < -180) {
            deltaHeading += 360;
        }

        int y = dim.yMid;
        int x = dim.xMid;

        y += MathHelper.floor(deltaPitch * dim.degreesPerPixel);
        x += MathHelper.floor(deltaHeading * dim.degreesPerPixel);

        if (y < dim.tFrame || y > dim.bFrame || x < dim.lFrame || x > dim.rFrame) {
            return;
        }

        int l = x - 3;
        int r = x + 3;
        int t = y - 3;
        int b = y + 3;

        Color color = gpws.getGPWSLampColor();
        drawVerticalLine(context, l, t, b, color);
        drawVerticalLine(context, r, t, b, color);

        drawHorizontalLine(context, l, r, t, color);
        drawHorizontalLine(context, l, r, b, color);

        drawVerticalLine(context, x, t - 5, t, color);
        drawHorizontalLine(context, l - 4, l, y, color);
        drawHorizontalLine(context, r, r + 4, y, color);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("flightassistant.flight_path_short"), dim.xMid, dim.yMid + 10, FAConfig.indicator().warningColor);
    }

    @Override
    public String getId() {
        return "flight_path";
    }
}
