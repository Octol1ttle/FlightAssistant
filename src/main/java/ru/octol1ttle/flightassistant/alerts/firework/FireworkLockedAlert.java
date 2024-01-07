package ru.octol1ttle.flightassistant.alerts.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.safety.WallCollisionComputer;

import static ru.octol1ttle.flightassistant.HudComponent.CONFIG;

public class FireworkLockedAlert extends AbstractAlert {
    private final WallCollisionComputer collision;

    public FireworkLockedAlert(WallCollisionComputer collision) {
        this.collision = collision;
    }

    @Override
    public boolean isTriggered() {
        return collision.collided;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, Text.translatable("alerts.flightassistant.firework.locked"), x, y,
                CONFIG.amberColor,
                !dismissed);
    }
}
