package net.torocraft.flighthud.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.IAlert;
import net.torocraft.flighthud.computers.FlightComputer;

public class AlertIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public AlertIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        for (IAlert alert : computer.alertController.activeAlerts) {
            alert.renderCentered(textRenderer, context, dim.wScreen, dim.hScreen / 2 + 10, computer.time.highlight);
        }
    }
}
