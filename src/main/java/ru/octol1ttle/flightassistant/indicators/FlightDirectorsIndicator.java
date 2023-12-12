package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.computers.autoflight.AutoFlightComputer;

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
            float deltaPitch = data.pitch + autoflight.getTargetPitch();
            float fdY = Math.max(dim.tFrame, Math.min(dim.bFrame, dim.yMid + i(deltaPitch * dim.degreesPerPixel)));
            drawHorizontalLine(context, dim.xMid - dim.wFrame * 0.1f, dim.xMid + dim.wFrame * 0.1f, fdY, CONFIG.adviceColor);
        }

        if (autoflight.getTargetHeading() != null) {
            float deltaHeading = autoflight.getTargetHeading() - data.flightHeading;
            if (deltaHeading < -180) {
                deltaHeading += 360;
            }

            float fdX = Math.max(dim.lFrame, Math.min(dim.rFrame, dim.xMid + i(deltaHeading * dim.degreesPerPixel)));
            drawVerticalLine(context, fdX, dim.yMid - dim.hFrame * 0.15f, dim.yMid + dim.hFrame * 0.15f, CONFIG.adviceColor);
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        Text text = Text.translatable("flightassistant.flight_directors_enabled");
        drawFont(textRenderer, context, text,
                (dim.wScreen - textRenderer.getWidth(text)) * 0.5f, dim.yMid - 20,
                CONFIG.alertColor);
    }

    @Override
    public String getId() {
        return "flt_dir";
    }
}
