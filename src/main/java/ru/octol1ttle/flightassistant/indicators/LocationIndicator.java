package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.computers.FlightComputer;

public class LocationIndicator extends HudComponent {

    private final FlightComputer computer;
    private final Dimensions dim;

    public LocationIndicator(FlightComputer computer, Dimensions dim) {
        this.computer = computer;
        this.dim = dim;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!CONFIG.location_showReadout) {
            return;
        }

        float x = dim.wScreen * CONFIG.location_x;
        float y = dim.hScreen * CONFIG.location_y;

        int xLoc = i((float) computer.position.x);
        int zLoc = i((float) computer.position.z);

        drawFont(textRenderer, context, String.format("%d / %d", xLoc, zLoc), x, y, CONFIG.color);
    }
}
