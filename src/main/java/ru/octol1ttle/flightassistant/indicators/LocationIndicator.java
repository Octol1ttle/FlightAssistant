package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.AirDataComputer;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class LocationIndicator extends HudComponent {

    private final Dimensions dim;
    private final AirDataComputer data;

    public LocationIndicator(Dimensions dim, AirDataComputer data) {
        this.dim = dim;
        this.data = data;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.hud().showCoordinates) {
            return;
        }

        float x = dim.lFrame + 15;
        float y = dim.bFrame;

        int xLoc = i((float) data.position.x);
        int zLoc = i((float) data.position.z);

        drawText(textRenderer, context, asText("%d / %d", xLoc, zLoc), x, y, FAConfig.hud().frameColor);
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        drawText(textRenderer, context, Text.translatable("flightassistant.location_short"),
                dim.lFrame + 15, dim.bFrame, FAConfig.hud().warningTextColor);
    }

    @Override
    public String getId() {
        return "location";
    }
}
