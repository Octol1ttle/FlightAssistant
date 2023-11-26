package ru.octol1ttle.flightassistant.alerts.fault;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.ComputerHost;
import ru.octol1ttle.flightassistant.computers.IComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class ComputerFaultAlert extends AbstractAlert {
    private final ComputerHost host;

    public ComputerFaultAlert(ComputerHost host) {
        this.host = host;
    }

    @Override
    public boolean isTriggered() {
        return !host.faulted.isEmpty();
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return host.faulted.contains(host.data) ? AlertSoundData.MASTER_WARNING : AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        int i = 0;
        for (IComputer computer : host.faulted) {
            boolean isADC = host.data.equals(computer);
            i += HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.fault.computers." + computer.getId()), x, y,
                    isADC ? CONFIG.alertColor : CONFIG.amberColor,
                    !dismissed && (highlight || !isADC));
            y += 10;
        }

        return i;
    }
}
