package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class FlightDirectorsIndicator extends HudComponent {
    private final Dimensions dim;
    private final AutoFlightComputer autoflight;
    private final AirDataComputer data;

    public FlightDirectorsIndicator(Dimensions dim, AutoFlightComputer autoflight, AirDataComputer data) {
        this.dim = dim;
        this.autoflight = autoflight;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!autoflight.flightDirectorsEnabled) {
            return;
        }

        if (autoflight.getTargetPitch() != null) {
            float deltaPitch = autoflight.getTargetPitch() - data.pitch;
            float fdY = MathHelper.clamp(dim.yMid - i(deltaPitch * dim.degreesPerPixel), dim.tFrame - 10, dim.bFrame + 10);
            drawHorizontalLine(context, dim.xMid - dim.wFrame * 0.1f, dim.xMid + dim.wFrame * 0.1f, fdY, FAConfig.hud().advisoryColor);
        }

        if (autoflight.getTargetHeading() != null) {
            float deltaHeading = autoflight.getTargetHeading() - data.heading;
            if (deltaHeading < -180.0f) {
                deltaHeading += 360.0f;
            }
            if (deltaHeading > 180.0f) {
                deltaHeading -= 360.0f;
            }

            float fdX = MathHelper.clamp(dim.xMid + i(deltaHeading * dim.degreesPerPixel), dim.lFrame + 10, dim.rFrame - 10);
            drawVerticalLine(context, fdX, dim.yMid - dim.hFrame * 0.15f, dim.yMid + dim.hFrame * 0.15f, FAConfig.hud().advisoryColor);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawMiddleAlignedText(textRenderer, context, Text.translatable("mode.flightassistant.auto.flight_directors"), dim.xMid, dim.yMid - 20, FAConfig.hud().warningColor);
    }

    @Override
    public String getId() {
        return "flt_dir";
    }
}
