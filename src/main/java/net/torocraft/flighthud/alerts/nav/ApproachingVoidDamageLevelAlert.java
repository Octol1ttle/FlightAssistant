package net.torocraft.flighthud.alerts.nav;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.torocraft.flighthud.HudComponent;
import net.torocraft.flighthud.alerts.AbstractAlert;
import net.torocraft.flighthud.alerts.AlertSoundData;
import net.torocraft.flighthud.alerts.ECAMSoundData;
import net.torocraft.flighthud.computers.FlightComputer;
import org.jetbrains.annotations.NotNull;

import static net.torocraft.flighthud.HudComponent.CONFIG;

public class ApproachingVoidDamageLevelAlert extends AbstractAlert {
    private final FlightComputer computer;

    public ApproachingVoidDamageLevelAlert(FlightComputer computer) {
        this.computer = computer;
    }

    @Override
    public boolean isTriggered() {
        return computer.groundLevel == computer.voidLevel && computer.altitude < computer.voidLevel + 8;
    }

    @Override
    public @NotNull AlertSoundData getAlertSoundData() {
        return ECAMSoundData.MASTER_WARNING;
    }

    @Override
    public int renderECAM(TextRenderer textRenderer, DrawContext context, float x, float y, boolean highlight) {
        return HudComponent.drawHighlightedFont(textRenderer, context, x, y,
                Text.translatable("alerts.flighthud.nav.approaching_void_damage_level"),
                CONFIG.alertColor,
                !dismissed && highlight);
    }
}
