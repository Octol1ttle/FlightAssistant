package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.computers.FlightComputer;

public class AlertIndicator extends HudComponent {
    private final Dimensions dim;
    private final FlightComputer computer;

    public AlertIndicator(FlightComputer computer, Dimensions dim) {
        this.dim = dim;
        this.computer = computer;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        boolean renderedCentered = false;
        float x = dim.lFrame + 5;
        float xRight = dim.rFrame - 5;
        float y = dim.tFrame + 15;
        float yRight = y - 10;

        for (AbstractAlert alert : computer.alert.activeAlerts) {
            if (!renderedCentered) {
                renderedCentered = alert.renderCentered(textRenderer, context, dim.wScreen,
                        dim.hScreen * 0.5f + 10, computer.time.highlight);
            }

            if (!alert.hidden) {
                y += 10 * alert.renderECAM(textRenderer, context, x, y, computer.time.highlight);
            }
        }
    }
}
