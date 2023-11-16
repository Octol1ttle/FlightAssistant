package ru.octol1ttle.flightassistant.alerts.nav.gpws;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.GPWSSoundData;
import ru.octol1ttle.flightassistant.computers.safety.GPWSComputer;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class ExcessiveTerrainClosureAlert extends AbstractAlert {
    private static final float TERRAIN_THRESHOLD = 7.5f;
    private static final AlertSoundData TERRAIN = new AlertSoundData(
            SoundEvent.of(new Identifier("flightassistant:terrain")),
            2,
            0.75f,
            false
    );
    private static final float PULL_UP_THRESHOLD = 5.0f;
    private static final float SECONDS_PER_TICK = 1.0f / TICKS_PER_SECOND;
    private static final float DELAY_ALERT_FOR = 0.5f;
    private final GPWSComputer gpws;
    private boolean delayFull = false;
    private float delay = 0.0f;

    public ExcessiveTerrainClosureAlert(GPWSComputer gpws) {
        this.gpws = gpws;
    }

    @Override
    public boolean isTriggered() {
        boolean triggered = gpws.descentImpactTime < 0.0f && gpws.terrainImpactTime >= 0.0f;
        if (triggered) {
            delay = MathHelper.clamp(delay + SECONDS_PER_TICK, 0.0f, DELAY_ALERT_FOR);
        } else {
            delay = MathHelper.clamp(delay - SECONDS_PER_TICK, 0.0f, DELAY_ALERT_FOR);
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
    public boolean renderCentered(TextRenderer textRenderer, DrawContext context, float width, float y, boolean highlight) {
        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            Text text = Text.translatable("alerts.flightassistant.pull_up");
            float startX = (width - textRenderer.getWidth(text)) * 0.5f;
            HudComponent.drawHighlightedFont(textRenderer, context, text, startX, y,
                    HudComponent.CONFIG.alertColor, highlight);

            return true;
        }

        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            Text text = Text.translatable("alerts.flightassistant.terrain_ahead");
            float startX = (width - textRenderer.getWidth(text)) * 0.5f;
            HudComponent.drawHighlightedFont(textRenderer, context, text, startX, y,
                    HudComponent.CONFIG.amberColor, highlight);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        if (gpws.terrainImpactTime <= PULL_UP_THRESHOLD) {
            return GPWSSoundData.PULL_UP;
        }
        if (gpws.terrainImpactTime <= TERRAIN_THRESHOLD) {
            return TERRAIN;
        }

        return AlertSoundData.EMPTY;
    }
}