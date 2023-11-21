package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;

public class AlertIndicator extends HudComponent {
    private final Dimensions dim;
    private final AlertController alert;
    private final TimeComputer time;

    public AlertIndicator(Dimensions dim, AlertController alert, TimeComputer time) {
        this.dim = dim;
        this.alert = alert;
        this.time = time;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        boolean renderedCentered = false;
        float x = dim.lFrame + 5;
        float y = dim.tFrame + 15;

        for (AbstractAlert alert : alert.activeAlerts) {
            if (!renderedCentered) {
                renderedCentered = alert.renderCentered(textRenderer, context, dim.wScreen,
                        dim.hScreen * 0.5f + 10, time.highlight);
            }

            if (!alert.hidden) {
                y += 10 * alert.renderECAM(textRenderer, context, x, y, time.highlight);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.fault.indicators.alert"), dim.lFrame + 5, dim.tFrame + 15,
                CONFIG.alertColor, time.highlight);
    }

    @Override
    public String getId() {
        return "alert";
    }
}
