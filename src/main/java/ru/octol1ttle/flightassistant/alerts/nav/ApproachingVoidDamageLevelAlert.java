package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.ECAMSoundData;
import ru.octol1ttle.flightassistant.computers.safety.VoidLevelComputer;

public class ApproachingVoidDamageLevelAlert extends AbstractAlert {

    private final VoidLevelComputer voidLevel;

    public ApproachingVoidDamageLevelAlert(VoidLevelComputer voidLevel) {
        this.voidLevel = voidLevel;
    }

    @Override
    public boolean isTriggered() {
        return voidLevel.approachingOrReachedDamageLevel();
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_WARNING;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        Text text = voidLevel.status == VoidLevelComputer.VoidLevelStatus.REACHED_DAMAGE_LEVEL
                ? Text.translatable("alerts.flightassistant.reached_void_damage_level")
                : Text.translatable("alerts.flightassistant.approaching_void_damage_level");

        return HudComponent.drawHighlightedFont(textRenderer, context, text, x, y,
                HudComponent.CONFIG.alertColor,
                !dismissed && highlight);
    }
}
