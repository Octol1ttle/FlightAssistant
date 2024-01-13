package ru.octol1ttle.flightassistant.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class ExcessiveTerrainClosureAlert extends AbstractAlert {
    private static final float TERRAIN_THRESHOLD = 7.5f;

    private static final float PULL_UP_THRESHOLD = 5.0f;
    private final GPWSComputer gpws;

    public ExcessiveTerrainClosureAlert(GPWSComputer gpws) {
        this.gpws = gpws;
    }

    @Override
    public boolean isTriggered() {
        return gpws.descentImpactTime < 0.0f && gpws.terrainImpactTime >= 0.0f;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            HudComponent.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.pull_up"), x, y,
                    CONFIG.alertColor, highlight);

            return true;
        }

        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            HudComponent.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.terrain_ahead"), x, y,
                    CONFIG.amberColor, highlight);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            return AlertSoundData.PULL_UP;
        }
        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            return AlertSoundData.TERRAIN;
        }

        return AlertSoundData.EMPTY;
    }
}