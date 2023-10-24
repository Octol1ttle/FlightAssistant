package ru.octol1ttle.flightassistant.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.octol1ttle.flightassistant.HudComponent;
import ru.octol1ttle.flightassistant.alerts.AbstractAlert;
import ru.octol1ttle.flightassistant.alerts.AlertSoundData;
import ru.octol1ttle.flightassistant.alerts.ECAMSoundData;
import ru.octol1ttle.flightassistant.computers.FlightComputer;
import ru.octol1ttle.flightassistant.computers.VoidDamageLevelComputer;

public class ApproachingVoidDamageLevelAlert extends AbstractAlert {
    private final FlightComputer computer;

    public ApproachingVoidDamageLevelAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.voidDamage.status >= VoidDamageLevelComputer.STATUS_APPROACHING_DAMAGE_LEVEL;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_WARNING;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        Text text = computer.voidDamage.status == VoidDamageLevelComputer.STATUS_REACHED_DAMAGE_LEVEL
                ? Text.translatable("alerts.flightassistant.nav.reached_void_damage_level")
                : Text.translatable("alerts.flightassistant.nav.approaching_void_damage_level");

        return HudComponent.drawHighlightedFont(textRenderer, context, x, y, text,
                HudComponent.CONFIG.alertColor,
                !dismissed && highlight);
    }
}
