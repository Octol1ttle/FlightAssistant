package ru.octol1ttle.flightassistant.indicators;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import ru.octol1ttle.flightassistant.Dimensions;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.AlertController;
import ru.octol1ttle.flightassistant.config.FAConfig;

public class AlertIndicator extends HudComponent {
    private final Dimensions dim;
    private final ComputerHost host;
    private final AlertController alert;
    private final TimeComputer time;

    public AlertIndicator(Dimensions dim, ComputerHost host, AlertController alert, TimeComputer time) {
        this.dim = dim;
        this.host = host;
        this.alert = alert;
        this.time = time;
    }

    @Override
    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!FAConfig.hud().alerts_show) {
            return;
        }
        if (host.faulted.contains(alert)) {
            renderFaulted(context, textRenderer, Text.translatable("alerts.flightassistant.fault.computers.alert_mgr"));
            return;
        }
        boolean renderedCentered = false;
        float x = dim.lFrame + 5;
        float y = dim.tFrame + 15;

        for (AbstractAlert alert : alert.activeAlerts) {
            if (!renderedCentered) {
                renderedCentered = alert.renderCentered(textRenderer, context, dim.xMid,
                        dim.yMid + 10, time.highlight);
            }

            if (!alert.hidden) {
                y += 10 * alert.renderECAM(textRenderer, context, x, y, time.highlight);
            }
        }
    }

    @Override
    public void renderFaulted(DrawContext context, TextRenderer textRenderer) {
        renderFaulted(context, textRenderer, Text.translatable("alerts.flightassistant.fault.indicators.alert"));
    }

    private void renderFaulted(DrawContext context, TextRenderer textRenderer, Text text) {
        HudComponent.drawHighlightedText(textRenderer, context, text, dim.lFrame + 5, dim.tFrame + 15,
                FAConfig.hud().warningTextColor, time.highlight);
    }

    @Override
    public String getId() {
        return "alert";
    }
}
