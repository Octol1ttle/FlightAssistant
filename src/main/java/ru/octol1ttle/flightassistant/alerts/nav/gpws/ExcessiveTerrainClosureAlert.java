package ru.octol1ttle.flightassistant.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.FAConfig;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.TimeComputer;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;



public class ExcessiveTerrainClosureAlert extends AbstractAlert {
    private static final float TERRAIN_THRESHOLD = 7.5f;

    private static final float PULL_UP_THRESHOLD = 5.0f;
    private static final float DELAY_ALERT_FOR = 0.5f;
    private final GPWSComputer gpws;
    private final TimeComputer time;
    private boolean delayFull = false;
    private float delay = 0.0f;

    public ExcessiveTerrainClosureAlert(GPWSComputer gpws, TimeComputer time) {
        this.gpws = gpws;
        this.time = time;
    }

    @Override
    public boolean isTriggered() {
        if (gpws.descentImpactTime >= 0.0f) {
            return false;
        }

        boolean triggered = gpws.terrainImpactTime >= 0.0f;
        if (triggered) {
            delay = MathHelper.clamp(delay + time.deltaTime, 0.0f, DELAY_ALERT_FOR);
        } else {
            delay = MathHelper.clamp(delay - time.deltaTime, 0.0f, DELAY_ALERT_FOR);
        }

        if (delay >= DELAY_ALERT_FOR) {
            delayFull = true;
        }
        if (delay <= 0.0f) {
            delayFull = false;
        }

        return delayFull;
    }

    @Override
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            HudComponent.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.pull_up"), x, y,
                    FAConfig.get().alertColor, highlight);

            return true;
        }

        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            HudComponent.drawHighlightedMiddleAlignedText(textRenderer, context, Text.translatable("alerts.flightassistant.terrain_ahead"), x, y,
                    FAConfig.get().amberColor, highlight);

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