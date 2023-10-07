package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.computers.FlightComputer;

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
