package ru.octol1ttle.flightassistant.alerts.firework;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.computers.autoflight.FireworkController;
import ru.octol1ttle.flightassistant.config.FAConfig;



public class FireworkUnsafeAlert extends AbstractAlert {

    private final FireworkController firework;

    public FireworkUnsafeAlert(FireworkController firework) {
        this.firework = firework;
    }

    @Override
    public boolean isTriggered() {
        return firework.unsafeFireworks;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return AlertSoundData.MASTER_CAUTION;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedText(textRenderer, context, Text.translatable("alerts.flightassistant.firework.unsafe"), x, y,
                FAConfig.hud().warningTextColor,
                !dismissed && highlight);
    }
}
